<svelte:options
  customElement={{
    tag: "eki-toast",
    extend: ekiComponentBase,
  }}
/>

<div
  class="absolute top-14 right-4 overflow-hidden z-[1073] flex flex-col gap-2"
>
  {#each toasts as toast}
    <div class={toast.class} transition:fly={{ x: 100 }}>
      <div class="flex gap-1 flex-col{toast.title ? ' mt-2' : ''}">
        {#if toast.title}
          <span class="text-sm font-medium">{toast.title}</span>
        {/if}
        <p
          class="break-word"
          class:text-sm={toast.type}
          class:text-xs={!toast.type}
        >
          <span>
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
    </div>{/each}
</div>

<script lang="ts">
  import { fly } from "svelte/transition";
  import { ekiComponentBase } from "../../lib/eki-component-base";
  import type { Toast } from "./toast.model";
  // Component reference provided by component base
  export let component;
  let toasts: Toast[] = [];
  let id = 0;
  const baseToastClass =
    "border text-eki-dark-blue-text rounded-lg pl-6 pr-[10px] grid grid-cols-[1fr_24px] gap-2 w-[335px] left-0";
  const closeToast = (toast: Toast) => {
    toasts = toasts.filter((existingToast) => existingToast.id !== toast.id);
    component.dispatchEvent(
      new CustomEvent("eki-toast-closed", {
        bubbles: true,
        composed: true,
        detail: toast,
      }),
    );
  };

  export const addToast = (toast: Toast) => {
    toast.isVisible = true;
    toast.id ??= id++;
    const additionalClasses =
      [
        toast.type === "error" && "bg-eki-light-red border-eki-red py-3",
        toast.type === "success" && "bg-eki-light-green border-eki-green py-3",
        toast.type === "warning" &&
          "bg-eki-light-warning border-eki-warning py-3",
      ]
        .filter(Boolean)
        .join(" ") || "bg-eki-white border-eki-light-blue pt-3 pb-5";
    toast.class = `${baseToastClass} ${additionalClasses}`;
    toasts = [...toasts, toast];
    component.dispatchEvent(
      new CustomEvent("eki-toast-opened", {
        bubbles: true,
        composed: true,
        detail: toast,
      }),
    );
  };
</script>
