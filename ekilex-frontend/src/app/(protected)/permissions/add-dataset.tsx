"use client";

import { Loader } from "@/components/loader/loader";
import { Modal } from "@/components/modal/modal";
import { useRouter } from "next/navigation";
import { useState, useTransition } from "react";
import { addDataset } from "./actions";

interface AddDatasetProps {
  // User should not be able to add datasets to themselves
  isDisabled: boolean;
  targetUserId: number;
  datasetOptions: { label: string; value: string }[];
  authOptions: { label: string; value: string }[];
  languageOptions: { label: string; value: string }[];
}

export default function AddDataset({
  isDisabled,
  targetUserId,
  datasetOptions,
  authOptions,
  languageOptions,
}: AddDatasetProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isPending, startTransition] = useTransition();
  const router = useRouter();

  const onSubmit = async (formData: FormData) => {
    startTransition(async () => {
      const result = await addDataset(targetUserId, formData);
      if (result) {
        setIsModalOpen(false);
        router.refresh();
      }
    });
  };
  return (
    <>
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Lisa luba"
      >
        <form className="mb-3 flex flex-col gap-3" action={onSubmit}>
          <div className="flex flex-col gap-2">
            <label className="text-eki-gray-400" htmlFor="dataset-select">
              Sõnakogu
            </label>
            <select
              className="border border-eki-gray-400 px-3 py-2 rounded-md"
              name="datasetCode"
              id="dataset-select"
              defaultValue=""
            >
              <option value="" disabled>
                Vali
              </option>
              {datasetOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="flex flex-col gap-2">
              <label className="text-eki-gray-400" htmlFor="auth-select">
                Õigus
              </label>
              <select
                className="border border-eki-gray-400 px-3 py-2 rounded-md"
                name="authOp"
                id="auth-select"
                defaultValue=""
              >
                <option value="" disabled>
                  Vali
                </option>
                {authOptions.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>
            <div className="flex flex-col gap-2">
              <label className="text-eki-gray-400" htmlFor="lang-select">
                Sõnakogu
              </label>
              <select
                className="border border-eki-gray-400 px-3 py-2 rounded-md"
                name="authLang"
                id="lang-select"
                defaultValue=""
              >
                <option value="">Kõik keeled</option>
                {languageOptions.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>
          </div>
          <div className="flex gap-3 justify-end mt-2">
            {isPending ? (
              <Loader />
            ) : (
              <>
                <button
                  className="bg-eki-blue px-3 py-1 rounded-md text-white w-max"
                  type="submit"
                >
                  Lisa
                </button>
                <button
                  className="px-3 py-1 rounded-md text-eki-gray-400 w-max"
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                >
                  Katkesta
                </button>
              </>
            )}
          </div>
        </form>
      </Modal>
      <button
        className="bg-eki-success flex justify-center items-center w-5 h-5 rounded-md text-white text-lg font-medium enabled:hover:text-eki-blue disabled:bg-eki-gray-400"
        type="button"
        onClick={() => setIsModalOpen(true)}
        disabled={isDisabled}
        title="Lisa luba"
      >
        +
      </button>
    </>
  );
}
