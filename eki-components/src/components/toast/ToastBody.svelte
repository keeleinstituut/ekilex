<section class={toast.class} aria-labelledby={`toast-title-${toast.id} toast-body-${toast.id}`}  transition:fly={{ x: 100 }}>
  <div class="flex gap-1" class:flex-col={toast.body} class:mt-2={toast.title}>
    {#if toast.title}
      <p class="text-sm font-medium" id={`toast-title-${toast.id}`}>
        {toast.title}
        {#if !toast.body && toast.readMoreText}
          <a
            class="underline hover:no-underline"
            href={toast.readMoreUrl}
            target={toast.readMoreIsExternal ? "_blank" : undefined}
            rel={toast.readMoreIsExternal ? "noreferrer" : undefined}
            >{toast.readMoreText}</a
          >
        {/if}
      </p>
    {/if}
    {#if toast.body}
      <p class="break-word text-sm">
        <span id={`toast-body-${toast.id}`}>
          {toast.body}
        </span>
        {#if toast.readMoreText}
          <a
            class="underline hover:no-underline"
            href={toast.readMoreUrl}
            target={toast.readMoreIsExternal ? "_blank" : undefined}
            rel={toast.readMoreIsExternal ? "noreferrer" : undefined}
            >{toast.readMoreText}</a
          >
        {/if}
      </p>
    {/if}
  </div>
  <button
    class="w-6 h-6 flex justify-center items-center"
    type="button"
    aria-label={toast.closeLabel}
    on:click={() => closeToast(toast)}
  >
    <svg
      width="14"
      height="14"
      viewBox="0 0 14 14"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        d="M14 1.41L12.59 0L7 5.59L1.41 0L0 1.41L5.59 7L0 12.59L1.41 14L7 8.41L12.59 14L14 12.59L8.41 7L14 1.41Z"
        fill="currentColor"
      />
    </svg>
  </button>
</section>

<script lang="ts">
  import { fly } from "svelte/transition";
  import Toast from "./Toast.svelte";

  export let toast: Toast;
  export let closeToast: (toast: Toast) => void;
</script>
