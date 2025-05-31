import React from "react";
import Suggestions from "../Suggestions/Suggestions";
import QAItem from "./QAItem";
import { View, Alert } from "react-native";
import * as Clipboard from "expo-clipboard";

const ChatHistory = ({
  currentSession,
  loading,
  thinkingText,
  handleSearch,
  questionRefs,
  handleSuggestionClick,
}) => (
  <View style={{ flex: 1 }}>
    {(!currentSession ||
      !Array.isArray(currentSession.qaHistory) ||
      currentSession.qaHistory.length === 0) &&
    !loading ? (
      <Suggestions onSuggestionClick={handleSuggestionClick} />
    ) : (
      <View style={{ paddingHorizontal: 16, paddingTop: 10 }}>
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
);

export default ChatHistory;
