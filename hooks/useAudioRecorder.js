import { useState, useRef } from 'react';
import { Audio } from 'expo-av';
import { Alert } from 'react-native';
import { transcribeAudio } from '../services/TranscriptionService';

export default function useAudioRecorder() {
  const [isTranscribing, setIsTranscribing] = useState(false);
  const [recording, setRecording] = useState(null);
  const [recordingUri, setRecordingUri] = useState(null);
  const [error, setError] = useState(null);
  const [transcript, setTranscript] = useState('');

  const startRecording = async () => {
    try {
      const permission = await Audio.requestPermissionsAsync();
      if (!permission.granted) {
        Alert.alert('Permission not granted');
        return;
      }
      await Audio.setAudioModeAsync({
        allowsRecordingIOS: true,
        playsInSilentModeIOS: true,
      });
      const { recording } = await Audio.Recording.createAsync(
        Audio.RecordingOptionsPresets.HIGH_QUALITY
      );
      setRecording(recording);
      setRecordingUri(null);
      setError(null);
    } catch (err) {
      setError(err);
      Alert.alert('Failed to start recording', err.message);
    }
  };

  const stopRecording = async () => {
    if (!recording) return;
    try {
      await recording.stopAndUnloadAsync();
      const uri = recording.getURI();
      setRecordingUri(uri);
      setRecording(null);
      setIsTranscribing(true);
      const transcript = await transcribeAudio(uri);
      setIsTranscribing(false);
      setTranscript(transcript);
    } catch (err) {
      setError(err);
      setIsTranscribing(false);
      Alert.alert('Error stopping recording', err.message);
    }
    setRecording(null);
  };

  return {
    recording,
    isTranscribing,
    recordingUri,
    error,
    startRecording,
    stopRecording,
    transcript
  };
}
