import React from "react";
import HeaderComponent from "./HeaderComponent";
import Sidebar from "../Sidebar/Sidebar";

const SearchHeader = ({
  isSidebarOpen,
  setIsSidebarOpen,
  sessions,
  selectedSessionId,
  onSessionSelect,
  onNewSession,
  onSessionDelete,
  onClearAllSessions,
}) => (
  <>
    <HeaderComponent onMenuPress={() => setIsSidebarOpen(true)} />
    <Sidebar
      isVisible={isSidebarOpen}
      onClose={() => setIsSidebarOpen(false)}
      sessions={sessions}
      selectedSessionId={selectedSessionId}
      onSessionSelect={onSessionSelect}
      onNewSession={onNewSession}
      onSessionDelete={onSessionDelete}
      onClearAllSessions={onClearAllSessions}
    />
  </>
);

export default SearchHeader;
