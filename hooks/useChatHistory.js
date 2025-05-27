import { useState, useEffect } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { LayoutAnimation } from "react-native";

const HISTORY_SESSIONS_KEY = "chatSessions";
const HISTORY_SELECTED_KEY = "selectedSessionId";

const createNewSession = (headline = "New Chat") => ({
  id: Date.now().toString(),
  headline,
  qaHistory: [],
  createdAt: Date.now(), // Add timestamp for session creation
});

const useChatHistory = (scrollToQuestion) => {
  const [sessions, setSessions] = useState([]); // [{id, headline, qaHistory}]
  const [selectedSessionId, setSelectedSessionId] = useState(null);

  // Load sessions from AsyncStorage
  useEffect(() => {
    const loadHistory = async () => {
      try {
        const [savedSessions, savedSelected] = await Promise.all([
          AsyncStorage.getItem(HISTORY_SESSIONS_KEY),
          AsyncStorage.getItem(HISTORY_SELECTED_KEY),
        ]);
        if (savedSessions) setSessions(JSON.parse(savedSessions));
        if (savedSelected) setSelectedSessionId(savedSelected);
      } catch (error) {
        console.error("Error loading sessions:", error);
      }
    };
    loadHistory();
  }, []);

  // Save sessions to AsyncStorage
  useEffect(() => {
    const saveHistory = async () => {
      try {
        const multiSetArr = [[HISTORY_SESSIONS_KEY, JSON.stringify(sessions)]];
        if (selectedSessionId !== null && selectedSessionId !== undefined) {
          multiSetArr.push([HISTORY_SELECTED_KEY, selectedSessionId]);
        } else {
          await AsyncStorage.removeItem(HISTORY_SELECTED_KEY);
        }
        await AsyncStorage.multiSet(multiSetArr);
      } catch (error) {
        console.error("Error saving sessions:", error);
      }
    };
    saveHistory();
  }, [sessions, selectedSessionId]);

  // Create a new session and select it
  const createSession = (headline = "New Chat") => {
    const session = createNewSession(headline);
    setSessions((prev) => [session, ...prev]);
    setSelectedSessionId(session.id);
    return session.id;
  };

  // Select a session by id
  const selectSession = (id) => {
    setSelectedSessionId(id);
  };

  // Add a question to the current session
  const addQuestionToSession = (questionText, qaId) => {
    if (!selectedSessionId) createSession(questionText);
    setSessions((prev) =>
      prev.map((session) =>
        session.id === selectedSessionId
          ? {
              ...session,
              qaHistory: [
                ...session.qaHistory,
                {
                  id: qaId || Date.now().toString(),
                  question: questionText,
                  answer: "loading",
                  followupQuestions: [],
                },
              ],
              headline:
                session.qaHistory.length === 0
                  ? questionText
                  : session.headline,
            }
          : session
      )
    );
  };

  // Update answer in the current session
  const updateSessionWithAnswer = (qaId, answer, followupQuestions) => {
    LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
    setSessions((prev) =>
      prev.map((session) =>
        session.id === selectedSessionId
          ? {
              ...session,
              qaHistory: session.qaHistory.map((item) =>
                item.id === qaId
                  ? { ...item, answer, followupQuestions, isLoading: false }
                  : item
              ),
            }
          : session
      )
    );
    if (scrollToQuestion) scrollToQuestion(qaId);
  };

  // Update error in the current session
  const updateSessionWithError = (qaId, errorMessage) => {
    LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
    setSessions((prev) =>
      prev.map((session) =>
        session.id === selectedSessionId
          ? {
              ...session,
              qaHistory: session.qaHistory.map((qa) =>
                qa.id === qaId
                  ? { ...qa, answer: errorMessage, isLoading: false }
                  : qa
              ),
            }
          : session
      )
    );
  };

  // Delete a session
  const deleteSession = (id) => {
    setSessions((prev) => prev.filter((s) => s.id !== id));
    if (selectedSessionId === id) {
      // If deleted session is selected, select the next available or create new
      if (sessions.length > 1) {
        const next = sessions.find((s) => s.id !== id);
        setSelectedSessionId(next?.id || null);
      } else {
        setSelectedSessionId(null);
      }
    }
  };

  // Clear all sessions
  const clearAllSessions = () => {
    setSessions([]);
    setSelectedSessionId(null);
  };

  // Get current session's chat
  const currentSession = sessions.find((s) => s.id === selectedSessionId);

  return {
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
  };
};

export default useChatHistory;

// Example usage (in a component):
// const styles = getStyleForItem(true); // For a question
// const styles = getStyleForItem(false); // For a response
