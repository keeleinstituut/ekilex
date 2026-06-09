Hello world

<eki-toast bind:this={toastContainer}></eki-toast>

<button
  onclick={() =>
    toastContainer.addToast({
      title: "Version 1.41 Release Notes 19.06.2025",
      body: "- Parandatud viga, mis põhjustas kasutusnäite muutmisel selle märgenduse kadumise",
      closeLabel: "Sulge",
      readMoreText: "Loe lähemalt",
      readMoreUrl: "https://google.com",
      type: "error",
    })}>addToast</button
>

<script lang="ts">
  import "./dev.css";

  let toastContainer: HTMLElement & {
    addToast: (arg: {
      title?: string;
      body?: string;
      closeLabel: string;
      readMoreText?: string;
      readMoreUrl?: string;
      type?: "error" | "success" | "warning";
    }) => void;
  };
</script>
