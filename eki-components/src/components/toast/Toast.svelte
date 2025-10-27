<svelte:options
  customElement={{
    tag: "eki-toast",
    extend: ekiComponentBase,
  }}
/>

<div
  class="absolute top-14 min-[320px]:right-4 overflow-hidden z-[1073] flex flex-col gap-2"
>
  <div class="flex flex-col gap-2" role="alert">
    {#each alertToasts as toast (toast.id)}
      <ToastBody {toast} {closeToast} />
    {/each}
  </div>
  <div class="flex flex-col gap-2" role="status">
    {#each statusToasts as toast (toast.id)}
      <ToastBody {toast} {closeToast} />
    {/each}
  </div>
</div>

<script lang="ts">
  import { ekiComponentBase } from "../../lib/eki-component-base";
  import type { Toast } from "./toast.model";
  import ToastBody from "./ToastBody.svelte";
  // Component reference provided by component base
  export let component;
  let toasts: Toast[] = [];
  $: alertToasts = toasts.filter(
    (toast) => toast.type && ["error", "warning"].includes(toast.type),
  );
  $: statusToasts = toasts.filter(
    (toast) => !toast.type || !["error", "warning"].includes(toast.type),
  );
  let id = 0;
  const baseToastClass =
    "border text-eki-dark-blue-text rounded-lg pl-6 pr-[10px] grid grid-cols-[1fr_24px] gap-2 max-w-[335px] left-0";
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
