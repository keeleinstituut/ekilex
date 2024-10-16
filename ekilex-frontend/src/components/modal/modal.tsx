"use client";

import React, { PropsWithChildren } from "react";
import { createPortal } from "react-dom";

interface ModalProps extends PropsWithChildren {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  rootSelector?: string;
}

export const Modal = ({
  isOpen,
  onClose,
  title,
  children,
  rootSelector,
}: ModalProps) => {
  if (!isOpen) return null;

  return createPortal(
    <div className="fixed inset-0 bg-gray-900 bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-lg w-11/12 md:w-1/3 p-6">
        <div className="flex justify-between items-center">
          <h2 className="text-xl font-semibold">{title}</h2>
          <button
            className="text-gray-500 hover:text-gray-700 flex justify-center items-center w-6 h-6 text-2xl font-bold"
            onClick={onClose}
            title="Sulge"
          >
            &times;
          </button>
        </div>
        <div className="mt-4">{children}</div>
      </div>
    </div>,
    rootSelector
      ? (document.querySelector(rootSelector) as Element)
      : document.body
  );
};
