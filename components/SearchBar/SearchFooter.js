import React from "react";
import SearchInputBar from "./SearchInputBar";
import FeedbackModalComponent from "./FeedbackModalComponent";

const SearchFooter = ({
  query,
  setQuery,
  searchButtonScale,
  isKeyboardVisible,
  keyboardHeight,
  loading,
  handleSearch,
  handleStopResponse,
  stopRecording,
  startRecording,
  recording,
  isTranscribing,
  showFeedbackPopup,
  setShowFeedbackPopup,
}) => (
  <>
    <SearchInputBar
      query={query}
      onQueryChange={setQuery}
      searchButtonScale={searchButtonScale}
      isKeyboardVisible={isKeyboardVisible}
      keyboardHeight={keyboardHeight}
      loading={loading}
      onSearchSubmit={handleSearch}
      onStopResponse={handleStopResponse}
      stopRecording={stopRecording}
      startRecording={startRecording}
      recording={recording}
      isTranscribing={isTranscribing}
    />
    <FeedbackModalComponent
      visible={showFeedbackPopup}
      onClose={() => setShowFeedbackPopup(false)}
      onSubmitFeedback={(feedback) => {
        console.log("Feedback submitted:", feedback);
        setShowFeedbackPopup(false);
      }}
    />
  </>
);

export default SearchFooter;
