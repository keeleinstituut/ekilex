function announceForScreenReader(text) {
  const announcer = document.getElementById('aria-live-announcer');
  if (!announcer) {
    console.error('Attempted to announce for screen reader but could not find announcer element');
    return;
  }
  announcer.textContent = text;
}
