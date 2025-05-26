import { useState, useRef, useEffect } from 'react';
import { Audio } from 'expo-av';
import { Alert, Linking, Platform } from 'react-native';
import { transcribeAudio } from '../services/TranscriptionService'; // Assuming TranscriptionService is in services

const useAudioRecorder = (onTranscriptionComplete) => {
  const [isListening, setIsListening] = useState(false);
  const [recording, setRecording] = useState(null);
  const [recordingStatus, setRecordingStatus] = useState('idle'); // idle, recording, stopped, error
  const recordingRef = useRef(null);
  const recordingTimeoutRef = useRef(null);

  useEffect(() => {
    // Cleanup function to stop recording and clear timeout on unmount
    return () => {
      if (recordingRef.current) {
        console.log('Cleaning up active recording on unmount...');
        recordingRef.current.stopAndUnloadAsync().catch(e => console.error('Error stopping recording on unmount:', e));
        recordingRef.current = null;
      }
      if (recordingTimeoutRef.current) {
        clearTimeout(recordingTimeoutRef.current);
      }
    };
  }, []);

  const startRecording = async () => {
    try {
      const { status } = await Audio.requestPermissionsAsync();
      if (status !== 'granted') {
        Alert.alert(
          'Permission required',
          'Please enable microphone access in settings',
          [
            { text: 'Cancel', style: 'cancel' },
            { text: 'Open Settings', onPress: () => Linking.openSettings() }
          ]
        );
        setRecordingStatus('error');
        return;
      }

      await Audio.setAudioModeAsync({
        allowsRecordingIOS: true,
        playsInSilentModeIOS: true,
        // interruptionModeIOS: Audio.INTERRUPTION_MODE_IOS_DO_NOT_MIX, // Optional: consider interruption modes
        // shouldDuckAndroid: true, // Optional
        // interruptionModeAndroid: Audio.INTERRUPTION_MODE_ANDROID_DO_NOT_MIX, // Optional
        // playThroughEarpieceAndroid: false, // Optional
      });

      console.log('Starting recording...');
      const newRecording = new Audio.Recording();
      await newRecording.prepareToRecordAsync(
        Platform.OS === 'android' 
          ? Audio.RecordingOptionsPresets.HIGH_QUALITY 
          : Audio.RecordingOptionsPresets.HIGH_QUALITY // Check Expo docs for iOS specific presets if needed, HIGH_QUALITY is generally good
          // { // More granular control if needed
          //   android: {
          //     extension: '.m4a',
          //     outputFormat: Audio.RECORDING_OPTION_ANDROID_OUTPUT_FORMAT_MPEG_4,
          //     audioEncoder: Audio.RECORDING_OPTION_ANDROID_AUDIO_ENCODER_AAC,
          //     sampleRate: 44100,
          //     numberOfChannels: 1,
          //     bitRate: 128000,
          //   },
          //   ios: {
          //     extension: '.m4a', // or .caf
          //     outputFormat: Audio.RECORDING_OPTION_IOS_OUTPUT_FORMAT_MPEG4AAC, // or other formats
          //     audioQuality: Audio.RECORDING_OPTION_IOS_AUDIO_QUALITY_HIGH,
          //     sampleRate: 44100,
          //     numberOfChannels: 1,
          //     bitRate: 128000,
          //     linearPCMBitDepth: 16,
          //     linearPCMIsBigEndian: false,
          //     linearPCMIsFloat: false,
          //   },
          // }
      );
      recordingRef.current = newRecording;
      setRecording(newRecording); // Keep for potential direct manipulation if needed, though ref is primary
      await newRecording.startAsync();
      setIsListening(true);
      setRecordingStatus('recording');
      console.log('Recording started');

      // Stop recording after 30 seconds
      recordingTimeoutRef.current = setTimeout(() => {
        console.log('Recording timeout reached.');
        stopRecording();
      }, 30000);

    } catch (error) {
      console.error('Failed to start recording:', error);
      Alert.alert('Error', 'Failed to start recording. Please try again.');
      setIsListening(false);
      setRecordingStatus('error');
      if(recordingRef.current){
        await recordingRef.current.stopAndUnloadAsync().catch(e => console.error('Cleanup error after start fail:', e));
        recordingRef.current = null;
      }
    }
  };

  const stopRecording = async () => {
    if (!recordingRef.current) {
      console.log('No active recording to stop (ref is null).');
      // Ensure states are reset if somehow stop is called without active recording
      setIsListening(false);
      setRecordingStatus('idle'); 
      return;
    }
    console.log('Stopping recording...');
    if (recordingTimeoutRef.current) {
      clearTimeout(recordingTimeoutRef.current);
      recordingTimeoutRef.current = null;
    }

    try {
      await recordingRef.current.stopAndUnloadAsync();
      const uri = recordingRef.current.getURI();
      recordingRef.current = null; // Clear the ref after stopping
      setRecording(null); // Clear state
      setIsListening(false);
      setRecordingStatus('stopped');
      console.log('Recording stopped and stored at', uri);

      if (uri) {
        const transcription = await transcribeAudio(uri);
        if (transcription && onTranscriptionComplete) {
          onTranscriptionComplete(transcription); // Pass only the transcription string
        }
      }
    } catch (error) {
      // Handle specific error: "Recording already unloaded"
      if (error.message && error.message.includes('Recording has already been unloaded')) {
        console.warn('Attempted to stop an already unloaded recording.');
      } else {
        console.error('Failed to stop recording:', error);
        Alert.alert('Error', 'Failed to stop or process recording.');
      }
      // Ensure states are reset even on error
      recordingRef.current = null;
      setRecording(null);
      setIsListening(false);
      setRecordingStatus('error');
    }
    // Ensure audio mode is reset (optional, depending on desired app behavior)
    // await Audio.setAudioModeAsync({
    //   allowsRecordingIOS: false,
    // });
  };

  const toggleRecording = () => {
    if (isListening) {
      stopRecording();
    } else {
      startRecording();
    }
  };

  return {
    isListening,
    recordingStatus,
    startRecording,
    stopRecording,
    toggleRecording,
  };
};

export default useAudioRecorder;
