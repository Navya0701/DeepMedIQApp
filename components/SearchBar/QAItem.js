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
  };  // Format answer text with HTML-style formatting
  const formatText = (text) => {
    if (!text) return null;
    
    // Process the text with the new formatter
    const formatAnswer = (text) => {
      return text
      .replace(/\n/g, "<br>") // Replace \n with <br>
      .replace(/\((.*?)\)/g, "<strong>$1</strong>") // Wrap text between () with <strong>
      .replace(/\\(.?)\\*/g, "<strong>$1</strong>") // Wrap text starting with ### with <h1> and <strong>
      .replace(/\#\#(.*?)/g, "<strong>$1</strong>") // Wrap text starting with ** with <span> for blue italicized text
      .replace(/\#\#\#(.*?)/g, "<span style='color: blue; font-style: italic;'>$1</span>"); // Wrap text starting with ** with <span> for blue italicized text
    };
    
    // Convert HTML-like formatted text to React Native components
    const htmlToComponents = (htmlText) => {
      if (!htmlText) return null;
      
      // Split by <br> tags to handle line breaks
      const lines = htmlText.split("<br>");
      
      return (
        <View>
          {lines.map((line, lineIndex) => {
            // Process the individual formatting in each line
            let components = [];
            let currentText = "";
            let currentIndex = 0;
            
            // Process the line to extract formatted sections
            while (currentIndex < line.length) {
              // Check for <strong> tags
              if (line.substring(currentIndex, currentIndex + 8) === "<strong>") {
                // Add any accumulated text before the tag
                if (currentText) {
                  components.push(
                    <Text key={`text-${lineIndex}-${components.length}`} style={styles.regularText}>
                      {currentText}
                    </Text>
                  );
                  currentText = "";
                }
                
                // Find closing tag
                const endIndex = line.indexOf("</strong>", currentIndex);
                if (endIndex !== -1) {
                  // Extract and add the bold text
                  const boldText = line.substring(currentIndex + 8, endIndex);
                  components.push(
                    <Text key={`bold-${lineIndex}-${components.length}`} style={styles.boldText}>
                      {boldText}
                    </Text>
                  );
                  currentIndex = endIndex + 9; // Move past the closing tag
                  continue;
                }
              }
              
              // Check for span with style tag (blue italic text)
              if (line.substring(currentIndex, currentIndex + 48) === "<span style='color: blue; font-style: italic;'>") {
                // Add any accumulated text before the tag
                if (currentText) {
                  components.push(
                    <Text key={`text-${lineIndex}-${components.length}`} style={styles.regularText}>
                      {currentText}
                    </Text>
                  );
                  currentText = "";
                }
                
                // Find closing tag
                const endIndex = line.indexOf("</span>", currentIndex);
                if (endIndex !== -1) {
                  // Extract and add the styled text
                  const styledText = line.substring(currentIndex + 48, endIndex);
                  components.push(
                    <Text key={`blue-${lineIndex}-${components.length}`} style={styles.hashtagText}>
                      {styledText}
                    </Text>
                  );
                  currentIndex = endIndex + 7; // Move past the closing tag
                  continue;
                }
              }
              
              // Accumulate regular text
              currentText += line[currentIndex];
              currentIndex++;
            }
            
            // Add any remaining text
            if (currentText) {
              components.push(
                <Text key={`text-${lineIndex}-${components.length}`} style={styles.regularText}>
                  {currentText}
                </Text>
              );
            }
            
            // Return the line wrapped in a View
            return (
              <View key={`line-${lineIndex}`} style={{ flexDirection: 'row', flexWrap: 'wrap', marginBottom: 8 }}>
                {components}
              </View>
            );
          })}
        </View>
      );
    };
    
    const formattedHtml = formatAnswer(text);
    return htmlToComponents(formattedHtml);
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
  },  regularText: {
    fontSize: 17,
    color: "#333",
    lineHeight: 26,
  },
  boldText: {
    fontSize: 17,
    fontWeight: "bold",
    color: "#333",
    lineHeight: 26,
  },
  hashtagText: {
    fontSize: 17,
    color: "blue",
    fontStyle: "italic",
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
