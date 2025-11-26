import React from 'react';
import { View, TouchableOpacity, Text, StyleSheet } from 'react-native';

const Suggestions = ({ onSuggestionClick }) => {
  const suggestions = [];

  return (
    <View style={styles.container}>
      {/* No suggestions to display */}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    gap: 12,
    padding: 16,
    marginTop: 20,
  },
  button: {
    backgroundColor: '#f8f9fa',
    padding: 16,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#eee',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },
  buttonText: {
    fontSize: 16,
    color: '#333',
    textAlign: 'left',
  },
});

export default Suggestions;
