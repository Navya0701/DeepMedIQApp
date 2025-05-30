import React, { useState, useEffect, useRef } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  Image,
  Animated,
  StyleSheet,
  Easing,
} from "react-native";
import FeedbackModalComponent from "./FeedbackModalComponent"; // Adjust the import based on your file structure
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
  const [feedbackVisible, setFeedbackVisible] = useState(false);
  const [feedbackType, setFeedbackType] = useState(null);

  // Best-of-best animation: springy, fade, scale, slide, shadow pop
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
  // Format text: bold for **text** or (text), bullets for lines starting with *
  // Enhanced formatText: if both ** and () are present in a line, only bold the outermost (first-appeared) one, leave inner as plain text
  const formatText = (text) => {
    if (!text) return null;
    const lines = text.split("\n");
    return lines.map((line, idx) => {
      // Header if line starts with ###
      if (line.trim().startsWith("###")) {
        return (
          <Text key={idx} style={{ fontSize: 22, fontWeight: "600", color: "#333", marginVertical: 8 }}>
            {line.replace(/^\s*###\s*/, "")}
          </Text>
        );
      }
      // Bullet point if line starts with *
      if (line.trim().startsWith("*")) {
        return (
          <View
            key={idx}
            style={{ flexDirection: "row", alignItems: "flex-start" }}
          >
            <Text style={{ fontSize: 16, marginRight: 6 }}>{"\u2022"}</Text>
            <Text style={{ flex: 1, fontSize: 16 }}>
              {line.replace(/^\s*\*\s*/, "")}
            </Text>
          </View>
        );
      }
      // Bold logic: only bold the outermost (first-appeared) of **...** or (...)
      let out = [];
      let i = 0;
      while (i < line.length) {
        if (line[i] === "*" && line[i + 1] === "*") {
          // Found **
          let end = line.indexOf("**", i + 2);
          if (end !== -1) {
            out.push(
              <Text key={i} style={{ fontWeight: "bold", fontSize: 16 }}>
                {line.substring(i + 2, end)}
              </Text>
            );            i = end + 2;
            continue;
          }
        }
        if (line[i] === "(") {
          let end = line.indexOf(")", i + 1);
          if (end !== -1) {
            out.push(
              <Text key={i} style={{ fontSize: 16 }}>
                {line.substring(i + 1, end)}
              </Text>
            );
            i = end + 1;
            continue;
          }
        }
        // Otherwise, just push the character wrapped in Text component
        let nextSpecial = Math.min(
          ...["**", "("].map((token) => {
            let idx = line.indexOf(token, i);
            return idx === -1 ? line.length : idx;
          })
        );
        if (nextSpecial === i) nextSpecial = i + 1;
        const textSegment = line.substring(i, nextSpecial);
        if (textSegment) {
          out.push(
            <Text key={`text-${i}`} style={{ fontSize: 16 }}>
              {textSegment}
            </Text>
          );
        }        i = nextSpecial;      }
      
      return (
        <View key={idx} style={{ flexDirection: 'row', flexWrap: 'wrap' }}>
          {out}
        </View>
      );
    });
  };

  // Add rotation animation for loading gif
  const rotateAnim = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    if (qa.answer === "loading") {
      Animated.loop(
        Animated.timing(rotateAnim, {
          toValue: 1,
          duration: 2000, // Slower rotation (was 1000)
          useNativeDriver: true,
          isInteraction: false,
        })
      ).start();
    } else {
      rotateAnim.stopAnimation();
      rotateAnim.setValue(0);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
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
        <Text style={styles.questionText}>{qa.question}</Text>
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
              {qa.followupQuestions.map((followup, fIndex) => (
                <TouchableOpacity
                  key={`${qa.id}-followup-${fIndex}`}
                  style={styles.followupButton}
                  onPress={() => onFollowupClick(followup.question)}
                >
                  <Text style={styles.followupButtonText}>
                    {followup.question}
                  </Text>
                </TouchableOpacity>
              ))}
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
  newMessage: {
    // Add any specific styling for new messages if needed
  },
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
  regularText: {
    fontSize: 17, // Increased from 16
    color: "#333",
    lineHeight: 26, // Increased line height
  },
  boldText: {
    fontSize: 17, // Matches regular text size
    fontWeight: "bold",
    color: "#333",
    lineHeight: 26,
  },
  parenthesesText: {
    fontSize: 17,
    fontStyle: "italic",
    color: "#333",
    lineHeight: 26,
  },
  hashtagText: {
    fontSize: 17,
    color: "#CB2323",
    lineHeight: 26,
  },
  followupContainer: {
    marginTop: 12,
  },
  followupButton: {
    backgroundColor: "#f0f0f0",
    paddingVertical: 8,
    paddingHorizontal: 12,
    borderRadius: 15,
    marginBottom: 6,
    alignSelf: "flex-start",
  },
  followupButtonText: {
    color: "#CB2323",
    fontSize: 15,
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
