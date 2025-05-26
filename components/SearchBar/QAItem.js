import React from 'react';
import { View, Text, TouchableOpacity, Image, ActivityIndicator, Animated, StyleSheet } from 'react-native';

const QAItem = ({
  qa,
  isNewMessage,
  thinkingText,
  currentFadeAnim,
  onCopy,
  onFollowupClick,
  questionRef,
  showSeparator,
}) => {

  const renderFormattedAnswer = (text) => {
    if (!text) return null;

    const parts = text.split(/(\*\*[^\*\*]*\*\*|\([^)]*\)|#\w+)/g);

    return parts.map((part, index) => {
      if (!part || part.trim() === "") return <Text key={index}> </Text>; // Handle empty or null parts

      // Check if the part is content within double asterisks
      if (part.startsWith('**') && part.endsWith('**')) {
        const content = part.substring(2, part.length - 2).replace(/\d+\./g, ""); // Remove numbering like '1.'
        return <Text key={index} style={styles.regularText}>{content}</Text>;
      }
      // Check if the part is content within parentheses
      if (part.startsWith('(') && part.endsWith(')')) {
        return <Text key={index} style={styles.regularText}>{part}</Text>;
      }
      // Check if the part is a hashtag word
      if (part.startsWith('#')) {
        return <Text key={index} style={styles.regularText}>{part}</Text>;
      }
      // Regular text
      return <Text key={index} style={styles.regularText}>{part}</Text>;
    });
  };

  return (
    <View style={[
      styles.qaItem,
      showSeparator && styles.qaItemWithBorder,
      isNewMessage && styles.newMessage,
    ]}>
      <View ref={questionRef} style={styles.questionContainer}>
        <Text style={styles.questionText}>{qa.question}</Text>
      </View>

      {qa.answer === 'loading' ? (
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#CB2323" />
          {isNewMessage && thinkingText && (
            <Text style={styles.thinkingText}>{thinkingText}</Text>
          )}
        </View>
      ) : (
        <Animated.View style={[
          styles.answerContainer,
          { opacity: currentFadeAnim ?? 1 },
        ]}>
          <View style={styles.answerContent}>
            <View style={styles.answerRow}>
              <Image
                source={require('../../assets/images/DeepMedIQ-small.jpeg')}
                style={styles.answerIcon}
              />
              <View style={styles.answerTextWrapper}>
                {renderFormattedAnswer(qa.answer)}
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
            <TouchableOpacity onPress={() => onCopy(qa.answer)}>
              <Image
                source={require('../../assets/images/copyimage.png')}
                style={styles.feedbackIcon}
              />
            </TouchableOpacity>
          </View>
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
    borderBottomColor: '#eee',
  },
  newMessage: {
    // Add any specific styling for new messages if needed
  },
  questionContainer: {
    padding: 12,
    borderRadius: 8,
    marginBottom: 8,
    backgroundColor: '#fff',
    borderWidth: 2,
    borderColor: '#000',
  },
  questionText: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
  },
  loadingContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  thinkingText: {
    marginTop: 10,
    fontSize: 14,
    color: '#555',
  },
  answerContainer: {
    padding: 12,
    borderRadius: 12,
    backgroundColor: '#f5f6fa',
    borderWidth: 1,
    borderColor: '#000',
  },
  answerContent: {
    borderRadius: 8,
    marginBottom: 8,
  },
  answerRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
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
    fontSize: 17,  // Increased from 16
    color: '#333',
    lineHeight: 26, // Increased line height
  },
  boldText: {
    fontSize: 17,  // Matches regular text size
    fontWeight: 'bold',
    color: '#333',
    lineHeight: 26,
  },
  parenthesesText: {
    fontSize: 17,
    fontStyle: 'italic',
    color: '#333',
    lineHeight: 26,
  },
  hashtagText: {
    fontSize: 17,
    color: '#CB2323',
    lineHeight: 26,
  },
  followupContainer: {
    marginTop: 12,
  },
  followupButton: {
    backgroundColor: '#f0f0f0',
    paddingVertical: 8,
    paddingHorizontal: 12,
    borderRadius: 15,
    marginBottom: 6,
    alignSelf: 'flex-start',
  },
  followupButtonText: {
    color: '#CB2323',
    fontSize: 15,
  },
  feedbackIcons: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    marginTop: 10,
  },
  feedbackIcon: {
    width: 20,
    height: 20,
    marginLeft: 15,
  },
});

export default QAItem;