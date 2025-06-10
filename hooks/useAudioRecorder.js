import { useState, useRef,useEffect } from 'react';
import { useAudioRecorder, AudioModule, RecordingPresets } from 'expo-audio';
import { Alert } from 'react-native';
import { transcribeAudio } from '../services/TranscriptionService';

export default function useAudioRecorderCustom() {
  const [isTranscribing, setIsTranscribing] = useState(false);
  const [recordingUri, setRecordingUri] = useState(null);
  const [error, setError] = useState(null);
  const [transcript, setTranscript] = useState('');

  const audioRecorder = useAudioRecorder(RecordingPresets.HIGH_QUALITY);
  const [isRecording, setIsRecording] = useState(false);

  const startRecording = async () => {
    try{
    await audioRecorder.prepareToRecordAsync();
        audioRecorder.record();
        setIsRecording(true);
        setRecordingUri('');
        setError(null);
    }catch (err) {
      setError(err);
      Alert.alert('Failed to start recording', err.message);
    }
  };

  const stopRecording = async () => {
    // The recording will be available on `audioRecorder.uri`.
    await audioRecorder.stop();
    setIsRecording(false);
     const uri = audioRecorder.uri || '';
    setRecordingUri(uri);
    console.log("Recording URI:", uri);
    
    if(uri !== ''){
      console.log("Starting transcription...");
      setIsTranscribing(true);
      const transcript = await transcribeAudio(uri);
      setIsTranscribing(false);
      setTranscript(transcript);
    } else {
      console.log("No recording URI available for transcription");
    }
    
  };

    useEffect(() => {
    (async () => {
      const status = await AudioModule.requestRecordingPermissionsAsync();
      if (!status.granted) {
        Alert.alert('Permission to access microphone was denied');
      }
    })();
  }, []);

 

  return {
    isRecording,
    isTranscribing,
    recordingUri,
    error,
    startRecording,
    stopRecording,
    transcript
  };
}
