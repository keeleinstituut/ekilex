Hello world

<Toast
  title="Versioon 1.37"
  body="Värsked täiendused ootavad sind."
  closeLabel="Sulge"
  readMoreText="Loe lähemalt"
  readMoreUrl="https://google.com"
  bind:this={toast}
></Toast>

<!-- <eki-toast
  title="Versioon 1.37"
  body="Värsked täiendused ootavad sind."
  close-label="Sulge"
  read-more-text="Loe lähemalt"
  read-more-url="https://google.com"
  bind:this={toast}
></eki-toast> -->

<button on:click={() => toast.setVisibility((prev) => !prev)}>toggle</button>

<eki-styles></eki-styles>

<script>
  import Toast from "./components/Toast.svelte";
  import "./dev.css";
  let toast;
</script>
