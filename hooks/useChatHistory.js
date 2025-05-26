import { useState, useEffect, useRef } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { LayoutAnimation, Animated } from 'react-native';

const HISTORY_QA_KEY = "qaHistory";
const HISTORY_SEARCH_KEY = "searchHistory";

const useChatHistory = (scrollToQuestion) => {
  const [qaHistory, setQaHistory] = useState([]);
  const [searchHistory, setSearchHistory] = useState([]);
  const [newMessageId, setNewMessageId] = useState(null);
  const [showPreviousHistory, setShowPreviousHistory] = useState(true);
  const fadeAnims = useRef(new Map()).current;

  // Load history from AsyncStorage
  useEffect(() => {
    const loadHistory = async () => {
      try {
        const [savedQaHistory, savedSearchHistory] = await Promise.all([
          AsyncStorage.getItem(HISTORY_QA_KEY),
          AsyncStorage.getItem(HISTORY_SEARCH_KEY)
        ]);
        if (savedQaHistory) setQaHistory(JSON.parse(savedQaHistory));
        if (savedSearchHistory) setSearchHistory(JSON.parse(savedSearchHistory));
      } catch (error) {
        console.error("Error loading history:", error);
      }
    };
    loadHistory();
  }, []);

  // Save history to AsyncStorage
  useEffect(() => {
    const saveHistory = async () => {
      try {
        await AsyncStorage.multiSet([
          [HISTORY_QA_KEY, JSON.stringify(qaHistory)],
          [HISTORY_SEARCH_KEY, JSON.stringify(searchHistory)]
        ]);
      } catch (error) {
        console.error("Error saving history:", error);
      }
    };
    if (qaHistory.length > 0 || searchHistory.length > 0) { // Only save if there's something to save
        saveHistory();
    }
  }, [qaHistory, searchHistory]);

  // Cleanup fadeAnims on unmount
  useEffect(() => {
    return () => fadeAnims.clear();
  }, [fadeAnims]);

  const addQuestionToHistory = (questionText) => {
    const newQa = {
      id: Date.now().toString(),
      question: questionText,
      answer: "loading",
      followupQuestions: []
    };
    setNewMessageId(newQa.id);
    // LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut); // Animate new question appearing
    setQaHistory(prev => [...prev, newQa]);
    setSearchHistory(prev => [...prev, questionText].slice(-100)); // Keep last 100 search terms
    setShowPreviousHistory(false); // Hide old messages while new one loads
    return newQa.id;
  };

  const updateHistoryWithAnswer = (qaId, answer, followupQuestions) => {
    LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
    setQaHistory((prevList) => 
      prevList.map((item) =>
        item.id === qaId
          ? { ...item, answer, followupQuestions, isLoading: false }
          : item
      )
    );
    setShowPreviousHistory(true);
    
    // Start fade-in animation for the new answer
    const newFadeAnim = new Animated.Value(0);
    fadeAnims.set(qaId, newFadeAnim);
    Animated.timing(newFadeAnim, {
      toValue: 1,
      duration: 800,
      useNativeDriver: true,
    }).start(() => {
        if (scrollToQuestion) scrollToQuestion(qaId); // Scroll after animation
    });
    setNewMessageId(null); // Clear new message ID after processing
  };

  const updateHistoryWithError = (qaId, errorMessage) => {
    LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
    setQaHistory(prev => 
      prev.map(qa => 
        qa.id === qaId ? { ...qa, answer: errorMessage, isLoading: false } : qa
      )
    );
    setShowPreviousHistory(true);
    setNewMessageId(null);
  };

const getStyleForItem = (isQuestion) => ({
  borderRadius: 18, // Slightly rounded border
  padding: 10,
  backgroundColor: isQuestion ? '#F5F5F5' : '#FFFFFF', // Light gray for questions, white for others
  borderWidth: 1, // 1px black border
  borderColor: '#000000', // Black border
  marginVertical: 5,
});

  const proxyUrl = "https://cors-anywhere.herokuapp.com/"; // Proxy to bypass CORS
  const apiUrl = `${proxyUrl}https://mcapisvc2.azurewebsites.net/chat`;

  return {
    qaHistory,
    searchHistory,
    newMessageId,
    showPreviousHistory,
    fadeAnims,
    addQuestionToHistory,
    updateHistoryWithAnswer,
    updateHistoryWithError,
    setSearchHistory, // Expose if direct manipulation is needed (e.g. from sidebar)
    setQaHistory, // Expose for potential direct manipulation (e.g. clearing history)
    getStyleForItem, // Expose style function
  };
};

export default useChatHistory;

// Example usage (in a component):
// const styles = getStyleForItem(true); // For a question
// const styles = getStyleForItem(false); // For a response
