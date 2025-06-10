import React from "react";
import {
  View,
  TextInput,
  TouchableOpacity,
  Image,
  StyleSheet,
  Animated,
  ActivityIndicator,
  Easing,
} from "react-native";

const SearchInputBar = ({
  query,
  onQueryChange,
  searchButtonScale,
  isKeyboardVisible,
  keyboardHeight,
  loading,
  onSearchSubmit,
  onStopResponse,
  startRecording,
  stopRecording,
  isRecording,
  isTranscribing
}) => {
  const [isFocused, setIsFocused] = React.useState(false);
  const translateYAnim = React.useRef(new Animated.Value(0)).current;

  React.useEffect(() => {
    Animated.timing(translateYAnim, {
      toValue: isKeyboardVisible ? -keyboardHeight : 0,
      duration: 320,
      easing: Easing.out(Easing.cubic),
      useNativeDriver: true,
    }).start();
  }, [isKeyboardVisible, keyboardHeight]);

  return (
    <Animated.View
      style={[
        styles.searchBarContainer,
        { transform: [{ translateY: translateYAnim }] },
      ]}
    >
      <View style={styles.searchBarRounded}>
        <View style={styles.inputWithMic}>
          <TextInput
            style={styles.searchInputRounded}
            placeholder="How can DeepMedIQ help you today..."
            placeholderTextColor="#889"
            value={query}
            multiline
            onChangeText={onQueryChange}
            onSubmitEditing={onSearchSubmit}
            onFocus={() => setIsFocused(true)}
            onBlur={() => setIsFocused(false)}
            editable
            pointerEvents="auto"
            importantForAccessibility="yes"
            allowFontScaling
            autoFocus={false}
            keyboardType="default"
            returnKeyType="send"
          />
          <TouchableOpacity
            onPress={isRecording ? stopRecording : (!isTranscribing ? startRecording : undefined)}
            style={{
              backgroundColor: 'white',
              padding: 15,
              marginLeft: 10,
              borderRadius: 50,
              alignItems: 'center',
              justifyContent: 'center',
              minWidth: 50,
              minHeight: 50,
            }}
            disabled={isTranscribing}
          >
            {isTranscribing ? (
              <ActivityIndicator size="small" color="#1976D2" />
            ) : !isRecording ? (
              <Image source={require("../../assets/images/mic.png")} style={{ width: 20, height: 20 }} />
            ) : (
              <Image source={require("../../assets/images/stop.png")} style={{ width: 20, height: 20 }} />
            )}
          </TouchableOpacity>
        </View>
        <Animated.View style={{ transform: [{ scale: searchButtonScale }] }}>
          <TouchableOpacity
            style={[
              styles.searchButton,
              loading && styles.searchButtonDisabled,
            ]}
            onPress={() => {
              if (loading) {
                if (onStopResponse) onStopResponse();
                return;
              }
              onSearchSubmit();
            }}
            disabled={false}
            activeOpacity={0.7}
            accessible
            accessibilityLabel="Send"
          >
            
              <Image
                source={require("../../assets/images/DeepMedIQ-small.jpeg")}
                style={styles.searchButtonImage}
              />
        
          </TouchableOpacity>
        </Animated.View>
      </View>
    </Animated.View>
  );
};

const styles = StyleSheet.create({
  searchBarContainer: {
    position: "absolute",
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: "#fff",
    borderTopWidth: 1,
    borderTopColor: "#eee",
    zIndex: 1000,
  },
  searchBarRounded: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#f5f5f5",
    borderRadius: 32,
    margin: 12,
    paddingHorizontal: 12,
    paddingVertical: 6,
    shadowColor: "#1976D2",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 4,
  },
  inputWithMic: {
    flex: 1,
    flexDirection: "row",
    alignItems: "center",
  },
  searchInputRounded: {
    flex: 1,
    backgroundColor: "#fff",
    borderRadius: 32,
    paddingVertical: 10,
    paddingHorizontal: 16,
    fontSize: 16,
    borderWidth: 0,
    color: "#222",
  },
  searchButton: {
    marginLeft: 12,
    padding: 8,
    backgroundColor: "#fff",
    borderRadius: 8,
    elevation: 2,
  },
  searchButtonDisabled: {
    opacity: 0.6,
  },
  searchButtonImage: {
    width: 32,
    height: 32,
    borderRadius: 16,
  },
  spinnerStyle: {
    width: 32,
    height: 32,
    justifyContent: "center",
    alignItems: "center",
  },
  spinnerCircle: {
    width: 22,
    height: 22,
    borderWidth: 3,
    borderColor: "#1976D2",
    borderTopColor: "transparent",
    borderRadius: 11,
    borderStyle: "solid",
    transform: [{ rotate: "0deg" }],
  },
});

export default SearchInputBar;
