import React, { useState, useEffect, useRef } from "react";
import {
  Platform,
  View,
  Text,
  StyleSheet,
  ScrollView,
  Keyboard,
  Alert,
  Animated,
  LayoutAnimation,
  UIManager,
  SafeAreaView,
} from "react-native";
import * as Clipboard from "expo-clipboard";

import Suggestions from "../Suggestions/Suggestions";
import Sidebar from "../Sidebar/Sidebar";
import QAItem from "./QAItem";
import HeaderComponent from "./HeaderComponent";
import SearchInputBar from "./SearchInputBar";
import FeedbackModalComponent from "./FeedbackModalComponent";

// Custom Hooks
import useAudioRecorder from "../../hooks/useAudioRecorder";
import useChatHistory from "../../hooks/useChatHistory";

// Services
import { fetchChatResponse } from "../../services/ChatService";

const SearchBar = () => {
  // State management for UI elements not covered by hooks
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [query, setQuery] = useState(""); // Current text in input bar
  const [showSuggestions, setShowSuggestions] = useState(true); // Initially show suggestions
  const [loading, setLoading] = useState(false); // For overall loading state, e.g., during API calls
  const [showFeedbackPopup, setShowFeedbackPopup] = useState(false);
  const [keyboardHeight, setKeyboardHeight] = useState(0);
  const [isKeyboardVisible, setIsKeyboardVisible] = useState(false);
  const [thinkingText, setThinkingText] = useState(""); // Moved from useChatHistory for direct control if needed by UI
  const [isTranscribing, setIsTranscribing] = useState(false);

  // Refs
  const scrollViewRef = useRef(null);
  const questionRefs = useRef({});
  const previousRecordingStatusRef = useRef(); // Ref to store previous recording status
  const abortControllerRef = useRef(null); // Add a ref to store the abort controller

  // Animation Values (passed to SearchInputBar)
  const micButtonScale = useRef(new Animated.Value(1)).current;
  const searchButtonScale = useRef(new Animated.Value(1)).current;

  // Remove all session/multi-session logic and restore to single chat session logic
  // Remove all references to sessionHistory, chatSessions, selectedSessionId, handleSidebarHistoryItemClick, handleSidebarHistoryDelete, handleClearAllChats
  // Use useChatHistory for qaHistory and searchHistory
  const {
    sessions,
    selectedSessionId,
    currentSession,
    createSession,
    selectSession,
    addQuestionToSession,
    updateSessionWithAnswer,
    updateSessionWithError,
    deleteSession,
    clearAllSessions,
  } = useChatHistory();

  // Custom Hook for Audio Recording
  const {
    isListening,
    toggleRecording,
    recordingStatus, // Destructure recordingStatus
  } = useAudioRecorder((transcription) => {
    setIsTranscribing(false); // Transcription complete or failed softly, hide loader
    if (transcription) {
      setQuery(transcription); // Set transcribed text to input
    } else {
      console.log("Transcription result was null or empty.");
      // Optionally, provide feedback to the user if transcription is empty but not an error
    }
    setLoading(false); // Ensure general loading is also false
  });

  // Effect to manage isTranscribing based on recordingStatus
  useEffect(() => {
    const previousStatus = previousRecordingStatusRef.current;

    if (recordingStatus === "stopped" && previousStatus === "recording") {
      // Recording has just stopped, now starting transcription process
      setIsTranscribing(true);
      setLoading(true); // Show general loading as transcription is an active process
    } else if (recordingStatus === "error" && isTranscribing) {
      // An error occurred while isTranscribing was true
      setIsTranscribing(false);
      setLoading(false);
    }

    // Update the ref *after* using its previous value
    previousRecordingStatusRef.current = recordingStatus;
  }, [recordingStatus, isTranscribing]); // Added isTranscribing to dependency for the error check

  // Enable LayoutAnimation on Android
  useEffect(() => {
    if (Platform.OS === "android") {
      if (UIManager.setLayoutAnimationEnabledExperimental) {
        UIManager.setLayoutAnimationEnabledExperimental(true);
      }
    }
  }, []);

  // Keyboard event listeners
  useEffect(() => {
    const keyboardWillShowListener = Keyboard.addListener(
      Platform.OS === "ios" ? "keyboardWillShow" : "keyboardDidShow",
      (e) => {
        setKeyboardHeight(e.endCoordinates.height);
        setIsKeyboardVisible(true);
      }
    );
    const keyboardWillHideListener = Keyboard.addListener(
      Platform.OS === "ios" ? "keyboardWillHide" : "keyboardDidHide",
      () => {
        setKeyboardHeight(0);
        setIsKeyboardVisible(false);
      }
    );
    return () => {
      keyboardWillShowListener.remove();
      keyboardWillHideListener.remove();
    };
  }, []);

  // When user submits a new question, add to the selected session's history
  const handleSearch = async (customQuery) => {
    const searchQuery =
      typeof customQuery === "string" && customQuery.trim()
        ? customQuery
        : query;
    if (!searchQuery.trim()) return;
    setQuery("");
    Keyboard.dismiss();
    setLoading(true);
    setThinkingText("Thinking...");
    setShowSuggestions(false);
    const qaId = Date.now().toString();
    addQuestionToSession(searchQuery, qaId);
    // Scroll to end as soon as input is given
    setTimeout(() => {
      if (scrollViewRef.current) {
        scrollViewRef.current.scrollToEnd({ animated: true });
      }
    }, 100);
    // Abort previous request if any
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }
    const controller = new AbortController();
    abortControllerRef.current = controller;
    try {
      const responseData = await fetchChatResponse(
        searchQuery,
        controller.signal
      );
      updateSessionWithAnswer(
        qaId,
        responseData?.answer ||
          "Failed to get a valid response from the server.",
        responseData?.followup_questions || []
      );
    } catch (error) {
      if (error.name === "AbortError") {
        updateSessionWithError(qaId, "Response stopped by user.");
      } else {
        updateSessionWithError(
          qaId,
          "Error fetching response. Please check your connection."
        );
      }
    } finally {
      setLoading(false);
      setThinkingText("");
      abortControllerRef.current = null;
    }
  };

  // Add a stop handler
  const handleStopResponse = () => {
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }
  };

  const handleSuggestionClick = (suggestion) => {
    setQuery(suggestion); // Set suggestion to query
    handleSearch(suggestion); // Then perform search
  };

  // Automatically create a session if none exists
  useEffect(() => {
    if (!currentSession && sessions.length === 0) {
      createSession();
    }
  }, [currentSession, sessions, createSession]);

  // Enable LayoutAnimation on Android
  useEffect(() => {
    if (Platform.OS === "android") {
      if (UIManager.setLayoutAnimationEnabledExperimental) {
        UIManager.setLayoutAnimationEnabledExperimental(true);
      }
    }
  }, []);

  // Keyboard event listeners
  useEffect(() => {
    const keyboardWillShowListener = Keyboard.addListener(
      Platform.OS === "ios" ? "keyboardWillShow" : "keyboardDidShow",
      (e) => {
        setKeyboardHeight(e.endCoordinates.height);
        setIsKeyboardVisible(true);
      }
    );
    const keyboardWillHideListener = Keyboard.addListener(
      Platform.OS === "ios" ? "keyboardWillHide" : "keyboardDidHide",
      () => {
        setKeyboardHeight(0);
        setIsKeyboardVisible(false);
      }
    );
    return () => {
      keyboardWillShowListener.remove();
      keyboardWillHideListener.remove();
    };
  }, []);

  // Handler for new chat window (when app icon is clicked in sidebar)
  const handleNewChatWindow = () => {
    createSession();
    setShowSuggestions(true);
    setQuery("");
    setIsSidebarOpen(false);
  };

  // Handler for selecting a session from sidebar
  const handleSessionSelect = (sessionId) => {
    selectSession(sessionId);
    setShowSuggestions(false);
    setIsSidebarOpen(false);
  };

  // Handler for deleting a session
  const handleSessionDelete = (sessionId) => {
    deleteSession(sessionId);
  };

  // Handler for clearing all sessions
  const handleClearAllSessions = () => {
    clearAllSessions();
  };

  // Defensive: fallback UI for white screen or empty session
  if (!currentSession) {
    return (
      <SafeAreaView style={styles.safeArea}>
        <View style={styles.container}>
          <HeaderComponent onMenuPress={() => setIsSidebarOpen(true)} />
          <Sidebar
            isVisible={isSidebarOpen}
            onClose={() => setIsSidebarOpen(false)}
            sessions={sessions}
            selectedSessionId={selectedSessionId}
            onSessionSelect={handleSessionSelect}
            onNewSession={handleNewChatWindow}
            onSessionDelete={handleSessionDelete}
            onClearAllSessions={handleClearAllSessions}
          />
          <View
            style={{ flex: 1, justifyContent: "center", alignItems: "center" }}
          >
            <Text>No chat session found. Please start a new chat.</Text>
          </View>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.container}>
        <HeaderComponent
          onMenuPress={() => {
            setIsSidebarOpen(true);
          }}
        />
        <Sidebar
          isVisible={isSidebarOpen}
          onClose={() => setIsSidebarOpen(false)}
          sessions={sessions}
          selectedSessionId={selectedSessionId}
          onSessionSelect={handleSessionSelect}
          onNewSession={handleNewChatWindow}
          onSessionDelete={handleSessionDelete}
          onClearAllSessions={handleClearAllSessions}
        />
        <ScrollView
          ref={scrollViewRef}
          contentContainerStyle={[
            styles.scrollContent,
            isKeyboardVisible && {
              paddingBottom:
                keyboardHeight + (Platform.OS === "ios" ? 100 : 120),
            },
          ]}
          keyboardShouldPersistTaps="handled"
        >
          <View style={styles.chatContainer}>
            {/* Robust fallback for empty or loading state */}
            {(!currentSession ||
              !Array.isArray(currentSession.qaHistory) ||
              currentSession.qaHistory.length === 0) &&
            !loading ? (
              <Suggestions onSuggestionClick={handleSuggestionClick} />
            ) : (
              <View style={styles.historyContainer}>
                {(currentSession?.qaHistory || []).map((qa, index) => (
                  <QAItem
                    key={qa.id}
                    qa={qa}
                    isNewMessage={
                      index === (currentSession?.qaHistory?.length || 0) - 1
                    }
                    thinkingText={
                      index === (currentSession?.qaHistory?.length || 0) - 1 &&
                      qa.answer === "loading"
                        ? thinkingText
                        : ""
                    }
                    currentFadeAnim={1}
                    onCopy={async (text) => {
                      try {
                        await Clipboard.setStringAsync(text);
                        Alert.alert("Copied!", "Text copied to clipboard");
                      } catch (error) {
                        Alert.alert("Error", "Failed to copy text");
                      }
                    }}
                    onFollowupClick={handleSearch}
                    questionRef={(ref) => {
                      if (ref) questionRefs.current[qa.id] = ref;
                    }}
                    showSeparator={
                      index !== (currentSession?.qaHistory?.length || 0) - 1
                    }
                  />
                ))}
              </View>
            )}
          </View>
        </ScrollView>
        <SearchInputBar
          query={query}
          onQueryChange={setQuery}
          onSearchSubmit={handleSearch}
          onStopResponse={handleStopResponse}
          onToggleRecording={() => {
            Animated.sequence([
              Animated.timing(micButtonScale, {
                toValue: 0.8,
                duration: 100,
                useNativeDriver: true,
              }),
              Animated.timing(micButtonScale, {
                toValue: 1,
                duration: 100,
                useNativeDriver: true,
              }),
            ]).start();
            toggleRecording();
          }}
          isListening={isListening}
          isTranscribing={isTranscribing}
          micButtonScale={micButtonScale}
          searchButtonScale={searchButtonScale}
          isKeyboardVisible={isKeyboardVisible}
          keyboardHeight={keyboardHeight}
          loading={loading}
        />
        <FeedbackModalComponent
          visible={showFeedbackPopup}
          onClose={() => setShowFeedbackPopup(false)}
          onSubmitFeedback={(feedback) => {
            console.log("Feedback submitted:", feedback);
            setShowFeedbackPopup(false);
          }}
        />
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: "#fff", // Or your app\'s background color
  },
  container: {
    flex: 1,
    backgroundColor: "#fff",
  },
  scrollContent: {
    paddingBottom: 100, // Default padding, adjusted by keyboard
  },
  chatContainer: {
    flex: 1,
    paddingBottom: Platform.OS === "ios" ? 20 : 20, // Consistent padding
  },
  historyContainer: {
    paddingHorizontal: 16, // Use horizontal padding for content alignment
    paddingTop: 10, // Add some space at the top
  },
});

export default SearchBar;
