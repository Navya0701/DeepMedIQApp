// QAItem.js
import React, { useState, useEffect, useRef } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  Image,
  Animated,
  StyleSheet,
  TextInput,
  Pressable,
  Platform,
} from "react-native";
import FeedbackModalComponent from "./FeedbackModalComponent";
import loadingGif2 from "../../assets/images/loadingimage-2.gif";

const QAItem = ({
  qa,
  isNewMessage,
  thinkingText,
  onCopy,
  onFollowupClick,
  questionRef,
  showSeparator,
}) => {
  // Small interactive followup pill component
  const FollowupPill = ({ text, onPress }) => {
    const [active, setActive] = React.useState(false);
    return (
      <Pressable
        onPress={() => onPress(text)}
        onPressIn={() => setActive(true)}
        onPressOut={() => setActive(false)}
        onHoverIn={() => setActive(true)}
        onHoverOut={() => setActive(false)}
        android_ripple={{ color: '#fdecea' }}
        style={({ pressed }) => [
          styles.followupButton,
          (active || pressed) && styles.followupButtonActive,
        ]}
      >
        <Text style={[styles.followupPointer, active && styles.followupPointerActive]}>›</Text>
        <Text
          numberOfLines={2}
          ellipsizeMode="tail"
          style={[styles.followupButtonText, active && styles.followupButtonTextActive, Platform.OS === 'web' && { cursor: 'pointer' }]}
        >
          {text}
        </Text>
      </Pressable>
    );
  };
  const [feedbackVisible, setFeedbackVisible] = useState(false);
  const [feedbackType, setFeedbackType] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editedQuestion, setEditedQuestion] = useState(qa.question);

  const cardAnim = useRef(new Animated.Value(0)).current;
  useEffect(() => {
    if (qa.answer && qa.answer !== "loading") {
      cardAnim.setValue(0);
      Animated.spring(cardAnim, {
        toValue: 1,
        friction: 7,
        tension: 60,
        useNativeDriver: true,
      }).start();
    } else {
      cardAnim.setValue(0);
    }
  }, [qa.answer]);

  const handleLike = () => {
    setFeedbackType("like");
    setFeedbackVisible(true);
  };
  const handleDislike = () => {
    setFeedbackType("dislike");
    setFeedbackVisible(true);
  };

  const handleEditQuestion = () => {
    setIsEditing(true);
  };

  const handleSaveEdit = () => {
    if (editedQuestion.trim() && editedQuestion.trim() !== qa.question) {
      onFollowupClick(editedQuestion.trim());
    }
    setIsEditing(false);
  };

  const handleCancelEdit = () => {
    setEditedQuestion(qa.question);
    setIsEditing(false);
  };

  // Enhanced formatter: preserves all \n, bolds (text), strong for ###, emphasize for **text**
  const formatText = (text) => {
    if (!text) return null;
    // Split by \n, preserving empty lines
    const lines = text.split('\n');
    return lines.map((line, idx) => {
      // If line is empty, render a blank line for spacing
      if (line.trim() === "") {
        return <Text key={`empty-${idx}`} style={{marginVertical: 6}}>{' '}</Text>;
      }

      // ### Header: strong
      if (line.trim().startsWith('###')) {
        return (
          <Text
            key={idx}
            style={{ fontSize: 20, fontWeight: 'bold', color: '#222', marginVertical: 8 }}
          >
            {line.replace(/^\s*###\s*/, '')}
          </Text>
        );
      }

      // Bullets for - or *
      if (line.trim().match(/^[-*]\s/)) {
        // Remove bullet, format rest of line
        const bulletContent = line.replace(/^[-*]\s*/, '');
        return (
          <View key={idx} style={{ flexDirection: 'row', alignItems: 'flex-start' }}>
            <Text style={{ fontSize: 16, marginRight: 6 }}>{'\u2022'}</Text>
            <Text style={{ flex: 1, fontSize: 16 }}>
              {formatInline(bulletContent, idx)}
            </Text>
          </View>
        );
      }

      // Regular line with inline formatting
      return (
        <Text key={idx} style={{ fontSize: 16, color: '#333', lineHeight: 26 }}>
          {formatInline(line, idx)}
        </Text>
      );
    });
  };

  // Inline formatter: bold (text), emphasize **text**
  const formatInline = (line, parentIdx) => {
    const result = [];
    let i = 0;
    let keyCounter = 0;
    while (i < line.length) {
      // Bold for (content)
      if (line[i] === '(') {
        let end = line.indexOf(')', i + 1);
        if (end !== -1) {
          result.push(
            <Text
              key={`bold-${parentIdx}-${keyCounter++}`}
              style={{ fontWeight: 'bold' }}
            >
              {line.substring(i + 1, end)}
            </Text>
          );
          i = end + 1;
          continue;
        }
      }
      // Emphasize for **content**
      if (line[i] === '*' && line[i + 1] === '*') {
        let end = line.indexOf('**', i + 2);
        if (end !== -1) {
          result.push(
            <Text
              key={`emph-${parentIdx}-${keyCounter++}`}
              style={{ fontStyle: 'italic', color: '#1976D2' }}
            >
              {line.substring(i + 2, end)}
            </Text>
          );
          i = end + 2;
          continue;
        }
      }
      // Normal text until next special
      let nextSpecial = line.length;
      const nextParen = line.indexOf('(', i);
      const nextAster = line.indexOf('**', i);
      if (nextParen !== -1 && nextParen < nextSpecial) nextSpecial = nextParen;
      if (nextAster !== -1 && nextAster < nextSpecial) nextSpecial = nextAster;
      const textSegment = line.substring(i, nextSpecial);
      if (textSegment) {
        result.push(
          <Text key={`plain-${parentIdx}-${keyCounter++}`}>{textSegment}</Text>
        );
      }
      i = nextSpecial === i ? i + 1 : nextSpecial;
    }
    return result;
  };

  const rotateAnim = useRef(new Animated.Value(0)).current;
  useEffect(() => {
    if (qa.answer === "loading") {
      Animated.loop(
        Animated.timing(rotateAnim, {
          toValue: 1,
          duration: 2000,
          useNativeDriver: true,
          isInteraction: false,
        })
      ).start();
    } else {
      rotateAnim.stopAnimation();
      rotateAnim.setValue(0);
    }
  }, [qa.answer]);

  return (
    <View
      style={[
        styles.qaItem,
        showSeparator && styles.qaItemWithBorder,
        isNewMessage && styles.newMessage,
      ]}
    >
      <View ref={questionRef} style={styles.questionContainer}>
        {isEditing ? (
          <View style={styles.editContainer}>
            <TextInput
              style={styles.editInput}
              value={editedQuestion}
              onChangeText={setEditedQuestion}
              multiline
              placeholder="Edit your question..."
              blurOnSubmit={false}
              scrollEnabled={false}
            />
            <View style={styles.editButtons}>
              <TouchableOpacity onPress={handleCancelEdit} style={styles.cancelButton}>
                <Text style={styles.cancelButtonText}>Cancel</Text>
              </TouchableOpacity>
              <TouchableOpacity onPress={handleSaveEdit} style={styles.saveButton}>
                <Text style={styles.saveButtonText}>Ask</Text>
              </TouchableOpacity>
            </View>
          </View>
        ) : (
          <>
            <Text style={styles.questionText}>{qa.question}</Text>
            <View style={styles.questionFeedbackIcons}>
              <TouchableOpacity onPress={() => onCopy(qa.question)}>
                <Image
                  source={require("../../assets/images/copyimage.png")}
                  style={styles.feedbackIcon}
                />
              </TouchableOpacity>
              <TouchableOpacity onPress={handleEditQuestion} style={styles.editButton}>
                <Image
                  source={require("../../assets/images/edit.png")}
                  style={[styles.feedbackIcon, styles.editIcon]}
                />
              </TouchableOpacity>
            </View>
          </>
        )}
      </View>
      {qa.answer === "loading" ? (
        <View style={styles.loadingContainer}>
          <Animated.Image
            source={loadingGif2}
            style={[
              styles.loadingGif,
              {
                transform: [
                  {
                    rotate: rotateAnim.interpolate({
                      inputRange: [0, 1],
                      outputRange: ["0deg", "360deg"],
                    }),
                  },
                ],
              },
            ]}
          />
          {isNewMessage && thinkingText && (
            <Text style={styles.thinkingText}>{thinkingText}</Text>
          )}
        </View>
      ) : (
        <Animated.View
          style={[
            styles.answerContainer,
            {
              opacity: cardAnim,
              transform: [
                {
                  scale: cardAnim.interpolate({
                    inputRange: [0, 1],
                    outputRange: [0.92, 1],
                  }),
                },
                {
                  translateY: cardAnim.interpolate({
                    inputRange: [0, 1],
                    outputRange: [48, 0],
                  }),
                },
              ],
              shadowOpacity: cardAnim.interpolate({
                inputRange: [0, 1],
                outputRange: [0, 0.18],
              }),
              shadowRadius: cardAnim.interpolate({
                inputRange: [0, 1],
                outputRange: [0, 12],
              }),
              shadowColor: "#1976D2",
              elevation: cardAnim.interpolate({
                inputRange: [0, 1],
                outputRange: [0, 8],
              }),
            },
          ]}
        >
          <View style={styles.answerContent}>
            <View style={styles.answerRow}>
              <Image
                source={require("../../assets/images/DeepMedIQ-small.jpeg")}
                style={styles.answerIcon}
              />
              <View style={styles.answerTextWrapper}>
                {formatText(qa.answer)}
              </View>
            </View>
          </View>

          {qa.followupQuestions?.length > 0 && (
            <View style={styles.followupContainer}>
              <Text style={styles.followupHeading}>Followup Questions</Text>
              {qa.followupQuestions.map((followup, fIndex) => {
                const text =
                  typeof followup === "string"
                    ? followup
                    : followup?.question || followup?.text || followup?.title || "";
                if (!text) return null;
                return (
                  <FollowupPill
                    key={`${qa.id}-followup-${fIndex}`}
                    text={text}
                    onPress={onFollowupClick}
                  />
                );
              })}
            </View>
          )}

          <View style={styles.feedbackIcons}>
            <TouchableOpacity onPress={handleLike}>
              <Image
                source={require("../../assets/images/Like.png")}
                style={styles.feedbackIcon}
              />
            </TouchableOpacity>
            <TouchableOpacity onPress={handleDislike}>
              <Image
                source={require("../../assets/images/dislike.png")}
                style={styles.feedbackIcon}
              />
            </TouchableOpacity>
            <TouchableOpacity onPress={() => onCopy(qa.answer)}>
              <Image
                source={require("../../assets/images/copyimage.png")}
                style={styles.feedbackIcon}
              />
            </TouchableOpacity>
          </View>
          {feedbackVisible && (
            <FeedbackModalComponent
              visible={feedbackVisible}
              onClose={() => setFeedbackVisible(false)}
              onSubmitFeedback={() => setFeedbackVisible(false)}
            />
          )}
        </Animated.View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  qaItem: {
    marginBottom: 15,
    paddingBottom: 15,
  },
  qaItemWithBorder: {
    borderBottomWidth: 1,
    borderBottomColor: "#eee",
  },
  newMessage: {},
  questionContainer: {
    padding: 12,
    borderRadius: 8,
    marginBottom: 8,
    backgroundColor: "#fff",
    borderWidth: 2,
    borderColor: "#000",
  },
  questionText: {
    fontSize: 16,
    color: "#333",
  },
  questionFeedbackIcons: {
    flexDirection: "row",
    justifyContent: "flex-end",
    marginTop: 8,
    marginBottom: 0,
  },
  editContainer: {
    flex: 1,
  },
  editInput: {
    borderWidth: 1,
    borderColor: "#ddd",
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
    color: "#333",
    height: 80,
    textAlignVertical: "top",
    backgroundColor: "#fff",
  },
  editButtons: {
    flexDirection: "row",
    justifyContent: "flex-end",
    marginTop: 12,
    gap: 10,
  },
  cancelButton: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: "#ddd",
    backgroundColor: "#fff",
  },
  cancelButtonText: {
    color: "#666",
    fontSize: 14,
    fontWeight: "500",
  },
  saveButton: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 6,
    backgroundColor: "#1976D2",
  },
  saveButtonText: {
    color: "#fff",
    fontSize: 14,
    fontWeight: "500",
  },
  editButton: {
    padding: 2,
    borderRadius: 4,
    marginLeft: 0,
  },
  editIcon: {
    tintColor: "#1976D2",
    opacity: 0.8,
  },
  loadingContainer: {
    alignItems: "center",
    justifyContent: "center",
    padding: 20,
  },
  loadingGif: {
    width: 40,
    height: 40,
    alignSelf: "center",
    marginBottom: 8,
  },
  thinkingText: {
    marginTop: 10,
    fontSize: 14,
    color: "#555",
  },
  answerContainer: {
    padding: 12,
    borderRadius: 12,
    backgroundColor: "#f5f6fa",
    borderWidth: 1,
    borderColor: "#000",
  },
  answerContent: {
    borderRadius: 8,
    marginBottom: 8,
  },
  answerRow: {
    flexDirection: "row",
    alignItems: "flex-start",
    gap: 8,
  },
  answerIcon: {
    width: 14,
    height: 14,
    marginTop: 4,
  },
  answerTextWrapper: {
    flex: 1,
  },
  followupContainer: {
    marginTop: 12,
    padding: 8,
    backgroundColor: '#fff',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#eee',
    // ensure the followups sit visually separate from the answer card
    marginBottom: 8,
  },
  followupHeading: {
    fontSize: 14,
    fontWeight: '700',
    color: '#333',
    marginBottom: 8,
  },
  followupButton: {
    backgroundColor: '#fff',
    paddingVertical: 10,
    paddingHorizontal: 12,
    borderRadius: 10,
    marginVertical: 6,
    alignSelf: 'stretch',
    width: '100%',
    flexDirection: 'row',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#eee',
  },
  followupButtonActive: {
    borderColor: '#CB2323',
    shadowColor: '#CB2323',
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.3,
    shadowRadius: 12,
    elevation: 8,
    transform: [{ translateY: -1 }],
  },
  followupPointer: {
    marginRight: 8,
    color: '#CB2323',
    fontSize: 16,
    fontWeight: '700',
    // keep pointer vertically centered
    alignSelf: 'center',
  },
  followupPointerActive: {
    color: '#9B1A1A',
  },
  followupButtonText: {
    color: '#333',
    fontSize: 15,
    flex: 1,
    flexWrap: 'wrap',
  },
  followupButtonTextActive: {
    color: '#CB2323',
  },
  feedbackIcons: {
    flexDirection: "row",
    justifyContent: "flex-end",
    marginTop: 10,
  },
  feedbackIcon: {
    width: 20,
    height: 20,
    marginLeft: 15,
  },
});

export default QAItem;
