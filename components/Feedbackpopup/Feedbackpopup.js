import React, { useState } from 'react';
import {
  View,
  Modal,
  Text,
  TouchableOpacity,
  TextInput,
  StyleSheet,
  ScrollView
} from 'react-native';

const reasons = [
  "Great Response !",
  "Answer is not correct",
  "Answer needs to be elaborative",
  "Factually incorrect",
  "Missing key information",
  "Inappropriate, unsafe, or biased",
  "Other",
];

const FeedbackPopup = ({ isOpen, onClose, onSubmitFeedback }) => {
  const [feedback, setFeedback] = useState('');
  const [selectedReason, setSelectedReason] = useState('');

  const handleSubmit = () => {
    if (onSubmitFeedback) {
      onSubmitFeedback({ reason: selectedReason, feedback });
    }
    onClose();
  };

  return (
    <Modal visible={isOpen} animationType="slide" transparent>
      <View style={styles.overlay}>
        <View style={styles.popup}>
          <Text style={styles.title}>Submit Your Feedback</Text>
          <ScrollView style={{ maxHeight: 200 }}>
            {reasons.map((reason) => (
              <TouchableOpacity
                key={reason}
                style={styles.radioContainer}
                onPress={() => setSelectedReason(reason)}
              >
                <View style={[styles.radioCircle, selectedReason === reason && styles.selected]} />
                <Text style={styles.radioText}>{reason}</Text>
              </TouchableOpacity>
            ))}
          </ScrollView>
          <TextInput
            style={styles.textInput}
            placeholder="Tell us how to improve..."
            multiline
            numberOfLines={4}
            value={feedback}
            onChangeText={setFeedback}
          />
          <View style={styles.buttonRow}>
            <TouchableOpacity style={styles.button} onPress={handleSubmit}>
              <Text style={styles.buttonText}>Submit</Text>
            </TouchableOpacity>
            <TouchableOpacity style={[styles.button, styles.closeBtn]} onPress={onClose}>
              <Text style={styles.buttonText}>Close</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.4)',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
  },
  popup: {
    backgroundColor: 'white',
    padding: 20,
    borderRadius: 12,
    width: '100%',
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 12,
  },
  radioContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
  },
  radioCircle: {
    height: 18,
    width: 18,
    borderRadius: 9,
    borderWidth: 2,
    borderColor: '#333',
    marginRight: 10,
  },
  selected: {
    backgroundColor: '#333',
  },
  radioText: {
    fontSize: 16,
  },
  textInput: {
    marginTop: 12,
    borderColor: '#ccc',
    borderWidth: 1,
    borderRadius: 8,
    padding: 10,
    textAlignVertical: 'top',
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 16,
  },
  button: {
    backgroundColor: '#333',
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderRadius: 8,
  },
  closeBtn: {
    backgroundColor: '#777',
  },
  buttonText: {
    color: '#fff',
    fontWeight: 'bold',
  },
});

export default FeedbackPopup;
