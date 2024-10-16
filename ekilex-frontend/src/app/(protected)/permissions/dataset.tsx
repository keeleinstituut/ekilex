"use client";

import { PropsWithChildren, useState, useTransition } from "react";
import { deleteDataset } from "./actions";
import { useRouter } from "next/navigation";
import { Loader } from "@/components/loader/loader";

interface DatasetProps extends PropsWithChildren {
  // User should not be able to remove datasets from themselves
  isDisabled: boolean;
  permissionId: number;
}

export const Dataset = ({
  isDisabled,
  permissionId,
  children,
}: DatasetProps) => {
  const [isPending, startTransition] = useTransition();
  const [isConfirmationOpen, setIsConfirmationOpen] = useState(false);
  const router = useRouter();

  const handleClick = async () => {
    startTransition(async () => {
      if (isDisabled) {
        return;
      }
      const result = await deleteDataset(permissionId);
      if (result) {
        router.refresh();
      }
    });
  };
  return (
    <div className="flex gap-2 relative py-1">
      {children}
      {isPending ? (
        <Loader size="small" />
      ) : (
        <button
          className="bg-eki-warning font-bold text-sm px-1 rounded flex w-5 h-5 justify-center items-center disabled:bg-eki-gray-200"
          type="button"
          title="Eemalda"
          disabled={isDisabled}
          onClick={() => setIsConfirmationOpen(true)}
        >
          X
        </button>
      )}

      {isConfirmationOpen ? (
        <div className="absolute bg-white border border-eki-gray-400 left-[calc(100%+16px)] w-[150px] rounded-md overflow-hidden z-10">
          <div className="bg-eki-gray-100 p-2">Kinnita kustutamine</div>
          <div className="flex gap-2 p-2 justify-center">
            <button
              className="bg-eki-blue px-3 py-1 rounded-md text-white"
              type="button"
              onClick={handleClick}
            >
              Jah
            </button>
            <button
              className="bg-eki-gray-100 px-3 py-1 rounded border border-eki-gray-400"
              type="button"
              onClick={() => setIsConfirmationOpen(false)}
            >
              Ei
            </button>
          </div>
        </div>
      ) : null}
    </div>
  );
};
