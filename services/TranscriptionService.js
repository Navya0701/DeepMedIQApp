import axios from 'axios';
import Constants from 'expo-constants';
import { Alert } from 'react-native';

const DEEPGRAM_API_KEY = process.env.EXPO_PUBLIC_DEEPGRAM_API_KEY;


export const transcribeAudio = async (audioUri) => {
  console.log('Transcribing audio from URI:', audioUri);
  if (!DEEPGRAM_API_KEY) {
    Alert.alert('Error', 'Deepgram API Key is missing.');
    console.error('Deepgram API Key is missing');
    return null;
  }

  try {
    const response = await fetch(audioUri);
    const audioBlob = await response.blob();

    // MODIFICATION: Use FileReader to read Blob as ArrayBuffer
    const arrayBuffer = await new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onloadend = () => {
        if (reader.readyState === FileReader.DONE) {
          resolve(reader.result);
        }
      };
      reader.onerror = reject;
      reader.readAsArrayBuffer(audioBlob);
    });
    // END MODIFICATION

    const audioBytes = new Uint8Array(arrayBuffer);

    const params = {
      model: 'nova-3',
      smart_format: true,
    };
    const queryString = new URLSearchParams(params).toString();

    console.log('Deepgram API Key:', DEEPGRAM_API_KEY ? 'Present' : 'Missing');
    const headers = {
      'Authorization': `Token ${DEEPGRAM_API_KEY}`,
      'Content-Type': audioBlob.type || 'audio/m4a', // Use blob type or default
    };
    
    console.log('Request Headers for Transcription:', headers);
    console.log('Request URL for Transcription:', `https://api.deepgram.com/v1/listen?${queryString}`);

    const apiResponse = await axios.post(
      `https://api.deepgram.com/v1/listen?${queryString}`,
      audioBytes, // Axios might handle Uint8Array directly, or it might need a Buffer.
      { headers }
    );

    const transcription = apiResponse.data.results.channels[0].alternatives[0].transcript;
    return transcription;
  } catch (error) {
    console.error('Transcription error:', error.response?.data || error.message);
    console.error('Error status:', error.response?.status);
    console.error('Error headers:', error.response?.headers);
    Alert.alert('Error', `Failed to transcribe audio: ${error.response?.data?.message || error.message}`);
    return null;
  }
};
