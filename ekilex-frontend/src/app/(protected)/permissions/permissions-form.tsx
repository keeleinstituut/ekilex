"use client";
import { usePathname, useRouter } from "next/navigation";
import { FormEvent, useState } from "react";

export default function PermissionsForm({
  searchParams,
}: {
  searchParams: {
    [key: string]: string;
  };
}) {
  const router = useRouter();
  const pathname = usePathname();
  const [state, setState] = useState({
    userNameFilter: searchParams.userNameFilter,
  });
  const handleSubmit = (event: FormEvent) => {
    event.preventDefault();
    const params = Object.entries(state).reduce((acc, [key, val]) => {
      if (val) {
        acc += `${key}=${val}`;
      }
      return acc;
    }, "");

    const queryParams = params.length ? `?${params}` : "";
    // Route to new url with new queryparams
    router.push(`${pathname}${queryParams}`);
  };
  return (
    <form className="flex gap-4" onSubmit={handleSubmit}>
      <input
        className="px-3 py-1 rounded-sm text-sm border border-solid border-gray-500"
        type="text"
        name="userNameFilter"
        value={state.userNameFilter}
        onChange={(e) =>
          setState((prev) => ({ ...prev, userNameFilter: e.target.value }))
        }
        placeholder="Nimi, e-post või selle osa"
      />
      <button
        className="bg-eki-blue px-3 py-1 lg:col-start-2 lg:ml-2 rounded-md text-white w-max"
        type="submit"
      >
        Otsi
      </button>
    </form>
  );
}
