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
  SafeAreaView
} from "react-native";
import * as Clipboard from 'expo-clipboard';

import Suggestions from "../Suggestions/Suggestions";
import Sidebar from "../Sidebar/Sidebar";
import QAItem from './QAItem';
import HeaderComponent from './HeaderComponent';
import SearchInputBar from './SearchInputBar';
import FeedbackModalComponent from './FeedbackModalComponent';

// Custom Hooks
import useAudioRecorder from '../../hooks/useAudioRecorder';
import useChatHistory from '../../hooks/useChatHistory';

// Services
import { fetchChatResponse } from '../../services/ChatService';

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

  // Animation Values (passed to SearchInputBar)
  const micButtonScale = useRef(new Animated.Value(1)).current;
  const searchButtonScale = useRef(new Animated.Value(1)).current;

  // Custom Hook for Chat History Management
  const {
    qaHistory,
    searchHistory,
    newMessageId,
    showPreviousHistory,
    fadeAnims,
    addQuestionToHistory,
    updateHistoryWithAnswer,
    updateHistoryWithError,
    setSearchHistory: setSearchHistoryFromHook, // Renamed to avoid conflict
  } = useChatHistory(scrollToQuestion);

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
      console.log('Transcription result was null or empty.');
      // Optionally, provide feedback to the user if transcription is empty but not an error
    }
    setLoading(false); // Ensure general loading is also false
  });

  // Effect to manage isTranscribing based on recordingStatus
  useEffect(() => {
    const previousStatus = previousRecordingStatusRef.current;

    if (recordingStatus === 'stopped' && previousStatus === 'recording') {
      // Recording has just stopped, now starting transcription process
      setIsTranscribing(true);
      setLoading(true); // Show general loading as transcription is an active process
    } else if (recordingStatus === 'error' && isTranscribing) {
      // An error occurred while isTranscribing was true
      setIsTranscribing(false);
      setLoading(false);
    }

    // Update the ref *after* using its previous value
    previousRecordingStatusRef.current = recordingStatus;
  }, [recordingStatus, isTranscribing]); // Added isTranscribing to dependency for the error check

  // Enable LayoutAnimation on Android
  useEffect(() => {
    if (Platform.OS === 'android') {
      if (UIManager.setLayoutAnimationEnabledExperimental) {
        UIManager.setLayoutAnimationEnabledExperimental(true);
      }
    }
  }, []);

  // Keyboard event listeners
  useEffect(() => {
    const keyboardWillShowListener = Keyboard.addListener(
      Platform.OS === 'ios' ? 'keyboardWillShow' : 'keyboardDidShow',
      (e) => {
        setKeyboardHeight(e.endCoordinates.height);
        setIsKeyboardVisible(true);
      }
    );
    const keyboardWillHideListener = Keyboard.addListener(
      Platform.OS === 'ios' ? 'keyboardWillHide' : 'keyboardDidHide',
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
  
  // Auto-scrolling when new answers arrive (simplified)
  useEffect(() => {
    if (qaHistory.length > 0 && !loading && showPreviousHistory) {
      const lastQuestionId = qaHistory[qaHistory.length - 1].id;
      if (lastQuestionId) {
         // scrollToQuestion is called by useChatHistory after animation
      }
    }
  }, [qaHistory, loading, showPreviousHistory]);


  // Function to scroll to a specific question
  function scrollToQuestion(id) {
    setTimeout(() => {
      const questionElement = questionRefs.current[id];
      if (questionElement && scrollViewRef.current) {
        questionElement.measure((x, y, width, height, pageX, pageY) => {
          if (pageY > 0) {
            // Added check for pageY to prevent scrolling to 0,0 if measure fails initially
            // The offset (e.g., -100) can be adjusted based on header height or desired padding
            const yOffset = pageY - (Platform.OS === 'ios' ? 60 : 80); // Adjust as needed
            scrollViewRef.current.scrollTo({
              y: Math.max(0, yOffset),
              animated: false, // Instant scroll
            });
          }
        });
      }
    }, 150); // Slightly increased delay to ensure layout is complete
  }

  const handleSearch = async (customQuery) => {
    const searchQuery = (typeof customQuery === 'string' && customQuery.trim()) ? customQuery : query;
    if (!searchQuery.trim()) return;

    // Animate search button (if SearchInputBar doesn't handle this internally)
    Animated.sequence([
      Animated.timing(searchButtonScale, { toValue: 0.8, duration: 100, useNativeDriver: true }),
      Animated.timing(searchButtonScale, { toValue: 1, duration: 100, useNativeDriver: true })
    ]).start();
    
    // setShowInitialLanding(false); // This is now implicitly handled by qaHistory.length
    setQuery(""); // Clear input after search
    Keyboard.dismiss();
    setLoading(true);
    setThinkingText("Thinking...");//thinking animation
    setShowSuggestions(false); // Hide suggestions once search starts

    const currentQaId = addQuestionToHistory(searchQuery);
    
    try {
      const responseData = await fetchChatResponse(searchQuery);
      if (responseData && responseData.answer) {
        updateHistoryWithAnswer(currentQaId, responseData.answer, responseData.followup_questions || []);
      } else {
        // Handle cases where responseData is null or answer is missing
        const errorMessage = responseData?.error || "Failed to get a valid response from the server.";
        console.error("API response error or missing answer:", responseData);
        updateHistoryWithError(currentQaId, errorMessage);
      }
    } catch (error) {
      // This catch is for network errors or if fetchChatResponse throws
      console.error("Fetch error in handleSearch:", error);
      updateHistoryWithError(currentQaId, "Error fetching response. Please check your connection.");
    } finally {
      setLoading(false);
      setThinkingText("");
    }
  };

  const handleSuggestionClick = (suggestion) => {
    setQuery(suggestion); // Set suggestion to query
    handleSearch(suggestion); // Then perform search
  };

  const handleCopy = async (text) => {
    try {
      await Clipboard.setStringAsync(text);
      Alert.alert("Copied!", "Text copied to clipboard");
    } catch (error) {
      console.error("Copy failed:", error);
      Alert.alert("Error", "Failed to copy text");
    }
  };
  
  const handleSidebarHistoryItemClick = (historyItem) => {
    setQuery(historyItem); // Set the history item to the search bar
    handleSearch(historyItem); // Perform the search
    setIsSidebarOpen(false); // Close sidebar
  };


  return (
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.container}>
        <HeaderComponent onMenuPress={() => setIsSidebarOpen(true)} />

        <Sidebar 
          isVisible={isSidebarOpen}
          onClose={() => setIsSidebarOpen(false)}
          searchHistory={searchHistory} // from useChatHistory
          onHistoryItemClick={handleSidebarHistoryItemClick} // Use the new handler
        />

        <ScrollView
          ref={scrollViewRef}
          contentContainerStyle={[
            styles.scrollContent,
            isKeyboardVisible && { paddingBottom: keyboardHeight + (Platform.OS === 'ios' ? 100 : 120) } // Adjusted padding
          ]}
          keyboardShouldPersistTaps="handled"
        >
          <View style={styles.chatContainer}>
            {qaHistory.length === 0 && !loading ? ( // Show suggestions if history is empty and not loading
              <Suggestions onSuggestionClick={handleSuggestionClick} />
            ) : (
              <View style={styles.historyContainer}>
                {qaHistory.map((qa, index) => {
                  // Logic to show only new message or all history based on showPreviousHistory
                  if (!showPreviousHistory && qa.id !== newMessageId) {
                    return null;
                  }
                  const isLastItem = index === qaHistory.length - 1;
                  const isNewMessage = qa.id === newMessageId;
                  // Separator logic might need adjustment based on when showPreviousHistory is true
                  const showSeparator = !isLastItem && (showPreviousHistory || qa.answer !== "loading");

                  return (
                    <QAItem
                      key={qa.id}
                      qa={qa}
                      isNewMessage={isNewMessage}
                      thinkingText={isNewMessage && qa.answer === 'loading' ? thinkingText : ""}
                      currentFadeAnim={fadeAnims.get(qa.id)}
                      onCopy={handleCopy}
                      onFollowupClick={handleSearch}
                      questionRef={ref => {
                        if (ref) questionRefs.current[qa.id] = ref;
                      }}
                      showSeparator={showSeparator}
                    />
                  );
                })}
              </View>
            )}
          </View>
        </ScrollView>

        <SearchInputBar
          query={query}
          onQueryChange={setQuery}
          onSearchSubmit={handleSearch}
          onToggleRecording={() => {
            // Animate mic button (optional, can be kept or removed if animation is tied to isListening/isTranscribing elsewhere)
            Animated.sequence([
              Animated.timing(micButtonScale, { toValue: 0.8, duration: 100, useNativeDriver: true }),
              Animated.timing(micButtonScale, { toValue: 1, duration: 100, useNativeDriver: true })
            ]).start();
            toggleRecording(); // Simply call the hook's toggleRecording
            // No direct setLoading or setIsTranscribing here; useEffect handles it based on recordingStatus
          }}
          isListening={isListening}
          isTranscribing={isTranscribing} // Pass new state
          micButtonScale={micButtonScale}
          searchButtonScale={searchButtonScale}
          isKeyboardVisible={isKeyboardVisible}
          keyboardHeight={keyboardHeight}
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
    paddingBottom: Platform.OS === 'ios' ? 20 : 20, // Consistent padding
  },
  historyContainer: {
    paddingHorizontal: 16, // Use horizontal padding for content alignment
    paddingTop: 10, // Add some space at the top
  },
});

export default SearchBar;