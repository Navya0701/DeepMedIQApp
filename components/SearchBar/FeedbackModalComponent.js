import React from 'react';
import { Modal, View, StyleSheet } from 'react-native';
import FeedbackPopup from '../Feedbackpopup/Feedbackpopup'; // Assuming FeedbackPopup is in a sibling directory

const FeedbackModalComponent = ({ visible, onClose, onSubmitFeedback }) => {
  return (
    <Modal
      animationType="slide"
      transparent
      visible={visible}
      onRequestClose={onClose}
    >
      <View style={styles.modalContainer}>
        <FeedbackPopup
          onClose={onClose}
          onSubmitFeedback={onSubmitFeedback}
        />
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  modalContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0,0,0,0.5)',
  },
});

export default FeedbackModalComponent;
