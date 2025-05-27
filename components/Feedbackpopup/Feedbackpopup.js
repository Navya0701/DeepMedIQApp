import React, { useState } from "react";
import {
  View,
  Modal,
  Text,
  TouchableOpacity,
  TextInput,
  StyleSheet,
  ScrollView,
} from "react-native";

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
  const [feedback, setFeedback] = useState("");
  const [selectedReason, setSelectedReason] = useState("");

  const handleSubmit = () => {
    if (onSubmitFeedback) {
      onSubmitFeedback({ reason: selectedReason, feedback });
    }
    onClose();
  };

  return (
    <Modal visible={isOpen} animationType="slide" transparent>
      <View style={styles.overlay} pointerEvents="box-none">
        <View style={styles.popup}>
          <Text style={styles.title}>Submit Your Feedback</Text>
          <ScrollView
            style={styles.radioList}
            contentContainerStyle={{ paddingBottom: 0 }}
          >
            {reasons.map((reason) => (
              <TouchableOpacity
                key={reason}
                style={styles.radioContainer}
                onPress={() => setSelectedReason(reason)}
              >
                <View
                  style={[
                    styles.radioCircle,
                    selectedReason === reason && styles.selected,
                  ]}
                />
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
            <TouchableOpacity
              style={[styles.button, styles.closeBtn]}
              onPress={onClose}
            >
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
    backgroundColor: "rgba(0,0,0,0.4)", // shadow effect
    justifyContent: "center",
    alignItems: "center",
    padding: 16,
  },
  popup: {
    backgroundColor: "white",
    padding: 24,
    borderRadius: 16,
    width: 360,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.25,
    shadowRadius: 12,
    elevation: 8,
    alignItems: "stretch", // allow children to stretch
  },
  title: {
    fontSize: 20,
    fontWeight: "bold",
    marginBottom: 18,
    color: "#222",
    textAlign: "center",
  },
  radioList: {
    marginBottom: 8,
  },
  radioContainer: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 8,
    alignSelf: "flex-start",
    maxWidth: "100%",
  },
  radioCircle: {
    height: 18,
    width: 18,
    borderRadius: 9,
    borderWidth: 2,
    borderColor: "#333",
    marginRight: 10,
    backgroundColor: "#fff",
  },
  selected: {
    backgroundColor: "#333",
  },
  radioText: {
    fontSize: 16,
    color: "#222",
    flexShrink: 1,
    flexWrap: "wrap",
  },
  textInput: {
    marginTop: 12,
    borderColor: "#ccc",
    borderWidth: 1,
    borderRadius: 8,
    padding: 10,
    textAlignVertical: "top",
    minHeight: 60,
    width: "100%",
    fontSize: 15,
    backgroundColor: "#fafafa",
    color: "#222",
  },
  buttonRow: {
    flexDirection: "row",
    justifyContent: "center",
    marginTop: 18,
    width: "100%",
  },
  button: {
    backgroundColor: "#43b04a", // green for submit
    paddingVertical: 10,
    paddingHorizontal: 28,
    borderRadius: 8,
    marginHorizontal: 8,
    minWidth: 100,
    alignItems: "center",
  },
  closeBtn: {
    backgroundColor: "#e74c3c", // red for close
  },
  buttonText: {
    color: "#fff",
    fontWeight: "bold",
    fontSize: 16,
  },
});

export default FeedbackPopup;
