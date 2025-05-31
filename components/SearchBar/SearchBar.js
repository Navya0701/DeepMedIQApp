import React, { useState, useEffect, useRef } from "react";
import { Platform, View, Text, StyleSheet, ScrollView, Keyboard, Alert, Animated, SafeAreaView } from "react-native";
import * as Clipboard from "expo-clipboard";
import Suggestions from "../Suggestions/Suggestions";
import Sidebar from "../Sidebar/Sidebar";
import QAItem from "./QAItem";
import HeaderComponent from "./HeaderComponent";
import SearchInputBar from "./SearchInputBar";
import FeedbackModalComponent from "./FeedbackModalComponent";
import useChatHistory from "../../hooks/useChatHistory";
import useAudioRecorder from '../../hooks/useAudioRecorder';
import useKeyboardManager from '../../hooks/useKeyboardManager';
import { fetchChatResponse } from "../../services/ChatService";

const SearchBar = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(false);
  const [showFeedbackPopup, setShowFeedbackPopup] = useState(false);
  const [thinkingText, setThinkingText] = useState("");
  const { recording, isTranscribing, startRecording, stopRecording, transcript } = useAudioRecorder();
  const scrollViewRef = useRef(null);
  const questionRefs = useRef({});
  const abortControllerRef = useRef(null);
  const searchButtonScale = useRef(new Animated.Value(1)).current;
  const { sessions, selectedSessionId, currentSession, createSession, selectSession, addQuestionToSession, updateSessionWithAnswer, updateSessionWithError, deleteSession, clearAllSessions } = useChatHistory();
  const { keyboardHeight, isKeyboardVisible } = useKeyboardManager();

  // Ensure at least one session exists
  useEffect(() => {
    if (!sessions || sessions.length === 0) {
      createSession();
    }
  }, [sessions, createSession]);

  useEffect(() => { if(transcript) setQuery(transcript); }, [transcript]);

  const handleSearch = async (customQuery) => {
    const searchQuery = (typeof customQuery === "string" && customQuery.trim()) ? customQuery : query;
    if (!searchQuery.trim()) return;
    setQuery(""); Keyboard.dismiss(); setLoading(true); setThinkingText("Thinking...");
    const qaId = Date.now().toString(); addQuestionToSession(searchQuery, qaId);
    setTimeout(() => scrollViewRef.current?.scrollToEnd({ animated: true }), 100);
    abortControllerRef.current?.abort();
    const controller = new AbortController(); abortControllerRef.current = controller;
    try {
      const responseData = await fetchChatResponse(searchQuery, controller.signal);
      updateSessionWithAnswer(qaId, responseData?.answer || "Failed to get a valid response from the server.", responseData?.followup_questions || []);
    } catch (error) {
      updateSessionWithError(qaId, error.name === "AbortError" ? "Response stopped by user." : "Error fetching response. Please check your connection.");
    } finally {
      setLoading(false); setThinkingText(""); abortControllerRef.current = null;
    }
  };

  if (!currentSession) return (
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.container}>
        <HeaderComponent onMenuPress={() => setIsSidebarOpen(true)} />
        <Sidebar
          isVisible={isSidebarOpen}
          onClose={() => setIsSidebarOpen(false)}
          sessions={sessions}
          selectedSessionId={selectedSessionId}
          onSessionSelect={id => { selectSession(id); setIsSidebarOpen(false); }}
          onNewSession={() => { createSession(); setIsSidebarOpen(false); setQuery(""); }}
          onSessionDelete={deleteSession}
          onClearAllSessions={clearAllSessions}
        />
        <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
          <Text>No chat session found. Please start a new chat.</Text>
        </View>
      </View>
    </SafeAreaView>
  );

  return (
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.container}>
        <HeaderComponent onMenuPress={() => setIsSidebarOpen(true)} />
        <Sidebar
          isVisible={isSidebarOpen}
          onClose={() => setIsSidebarOpen(false)}
          sessions={sessions}
          selectedSessionId={selectedSessionId}
          onSessionSelect={id => { selectSession(id); setIsSidebarOpen(false); }}
          onNewSession={() => { createSession(); setIsSidebarOpen(false); setQuery(""); }}
          onSessionDelete={deleteSession}
          onClearAllSessions={clearAllSessions}
        />
        <ScrollView
          ref={scrollViewRef}
          contentContainerStyle={[
            styles.scrollContent,
            isKeyboardVisible && { paddingBottom: keyboardHeight + (Platform.OS === "ios" ? 100 : 120) },
          ]}
          keyboardShouldPersistTaps="handled"
        >
          <View style={styles.chatContainer}>
            {(!currentSession?.qaHistory?.length && !loading) ? (
              <Suggestions onSuggestionClick={s => { setQuery(s); handleSearch(s); }} />
            ) : (
              <View style={styles.historyContainer}>
                {(currentSession?.qaHistory || []).map((qa, idx, arr) => (
                  <QAItem
                    key={qa.id}
                    qa={qa}
                    isNewMessage={idx === arr.length - 1}
                    thinkingText={idx === arr.length - 1 && qa.answer === "loading" ? thinkingText : ""}
                    currentFadeAnim={1}
                    onCopy={async text => { try { await Clipboard.setStringAsync(text); Alert.alert("Copied!", "Text copied to clipboard"); } catch { Alert.alert("Error", "Failed to copy text"); } }}
                    onFollowupClick={handleSearch}
                    questionRef={ref => { if (ref) questionRefs.current[qa.id] = ref; }}
                    showSeparator={idx !== arr.length - 1}
                  />
                ))}
              </View>
            )}
          </View>
        </ScrollView>
        <SearchInputBar
          query={query}
          onQueryChange={setQuery}
          searchButtonScale={searchButtonScale}
          isKeyboardVisible={isKeyboardVisible}
          keyboardHeight={keyboardHeight}
          loading={loading}
          onSearchSubmit={handleSearch}
          onStopResponse={() => abortControllerRef.current?.abort()}
          stopRecording={stopRecording}
          startRecording={startRecording}
          recording={recording}
          isTranscribing={isTranscribing}
        />
        <FeedbackModalComponent
          visible={showFeedbackPopup}
          onClose={() => setShowFeedbackPopup(false)}
          onSubmitFeedback={f => { console.log("Feedback submitted:", f); setShowFeedbackPopup(false); }}
        />
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: "#fff" },
  container: { flex: 1, backgroundColor: "#fff" },
  scrollContent: { paddingBottom: 100 },
  chatContainer: { flex: 1, paddingBottom: Platform.OS === "ios" ? 20 : 20 },
  historyContainer: { paddingHorizontal: 16, paddingTop: 10 },
});

export default SearchBar;
