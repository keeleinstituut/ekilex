Hello world

<!-- <Toast
  title="Versioon 1.37"
  body="Värsked täiendused ootavad sind."
  closeLabel="Sulge"
  readMoreText="Loe lähemalt"
  readMoreUrl="https://google.com"
  bind:this={toast}
></Toast> -->

<eki-toast bind:this={toastContainer}></eki-toast>

<button
  on:click={() =>
    toastContainer.addToast({
      title: "Sõnaveebi versiooni 1.38 täiendused 09.10.2024",
      body: "- Parandatud viga, mis põhjustas kasutusnäite muutmisel selle märgenduse kadumise",
      closeLabel: "Sulge",
      readMoreText: "Loe lähemalt",
      readMoreUrl: "https://google.com",
    })}>addToast</button
>

<script>
  // import Toast from "./components/Toast.svelte";
  import "./dev.css";
  /**
   * @type {{ addToast: (arg0: { title: string; body: string; closeLabel: string; readMoreText: string; readMoreUrl: string; }) => any; }}
   */
  let toastContainer;
</script>
