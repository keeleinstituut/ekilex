<svelte:options
  customElement={{
    tag: "eki-toast",
    extend: ekiComponentBase,
  }}
/>

<div class="absolute top-[53px] right-[15px] overflow-hidden">
  <div
    class="bg-eki-white border border-eki-light-blue text-eki-dark-blue-text rounded-lg pl-7 pr-2 py-4 grid grid-cols-[1fr_24px] gap-2 transition-[transform] w-[335px] left-0"
    class:translate-x-[calc(100%+15px)]={!isVisible}
  >
    <div class="flex gap-1 flex-col">
      <span class="text-sm font-medium">{title}</span>
      <p class="text-xs">
        <span>
          {body}
        </span>
        {#if readMoreText}
          <a
            class="underline"
            href={readMoreUrl}
            target={readMoreIsExternal ? "_blank" : undefined}
            rel={readMoreIsExternal ? "noreferrer" : undefined}
            >{readMoreText}</a
          >
        {/if}
      </p>
    </div>
    <button
      class="w-6 h-6 flex justify-center items-center"
      type="button"
      aria-label={closeLabel}
      on:click={() => setVisibility(false)}
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
  </div>
</div>

<script lang="ts">
  import { ekiComponentBase } from "../lib/eki-component-base";
  // Component reference provided by component base
  export let component;
  export let title;
  export let body;
  // Workaround for regular html not supporting camelCase attributes
  export let readMoreUrl = $$props["read-more-url"];
  export let readMoreText = $$props["read-more-text"];
  export let readMoreIsExternal = $$props["read-more-is-external"];
  export let closeLabel = $$props["close-label"];
  let isVisible = false;
  export const setVisibility = (
    input: boolean | ((state: boolean) => boolean),
  ) => {
    // Allow calling the method with a function that is provided previous state
    const newState = typeof input === "function" ? input(isVisible) : input;
    isVisible = newState;
    if (newState) {
      component.dispatchEvent(
        new CustomEvent("eki-toast-opened", { bubbles: true, composed: true }),
      );
    } else {
      component.dispatchEvent(
        new CustomEvent("eki-toast-closed", { bubbles: true, composed: true }),
      );
    }
  };
</script>
