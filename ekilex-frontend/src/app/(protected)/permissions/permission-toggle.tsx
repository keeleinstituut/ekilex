"use client";

import { UserPermissionType } from "@/lib/models/permissions/permissions";
import { ChangeEvent, useState, useTransition } from "react";
import { setPermission } from "./actions";
import { Loader } from "@/components/loader/loader";

interface PermissionToggleProps {
  // User should not be able to toggle their own permissions nor others if they are not an admin
  isDisabled: boolean;
  defaultValue: boolean;
  type: UserPermissionType;
  targetUserId: number;
  title: string;
}

export default function PermissionToggle({
  isDisabled,
  defaultValue,
  type,
  targetUserId,
  title,
}: PermissionToggleProps) {
  const [isPermissionEnabled, setIsPermissionEnabled] = useState(defaultValue);
  const [isPending, startTransition] = useTransition();

  const onToggle = (e: ChangeEvent<HTMLInputElement>) => {
    if (isDisabled) {
      return;
    }
    startTransition(async () => {
      const newState = await setPermission(
        targetUserId,
        type,
        e.target.checked
      );
      setIsPermissionEnabled(newState);
    });
  };
  return isPending ? (
    <Loader size="small"/>
  ) : (
    <input
      type="checkbox"
      name="permission-toggle"
      checked={isPermissionEnabled}
      onChange={onToggle}
      disabled={isDisabled}
      title={title}
      />
  )
}
