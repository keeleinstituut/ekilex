"use client";
import { Loader } from "@/components/loader/loader";
import { login } from "@/lib/actions/session";
import { useTransition } from "react";

export default function LoginForm() {
  const [isPending, startTransition] = useTransition();

  const onSubmit = async (formData: FormData) => {
    startTransition(() => {
      login(formData);
    });
  };
  if (isPending) {
    return <Loader/>;
  }
  return (
    <form action={onSubmit} className="p-5 gap-y-3 grid grid-cols-[144px_auto]">
      <div className="gap-2 flex col-start-1 -col-end-1 flex-col lg:flex-row">
        <label className="w-36 lg:text-right" htmlFor="login-email">
          E-posti address
        </label>
        <input
          className="px-3 py-1 rounded-sm text-sm border border-solid border-gray-500"
          type="email"
          name="email"
          placeholder="E-posti aadress"
          autoComplete="email"
          id="login-email"
        />
      </div>
      <div className="gap-2 flex col-start-1 -col-end-1 flex-col lg:flex-row">
        <label className="w-36 lg:text-right" htmlFor="login-password">
          Salasõna
        </label>
        <input
          className="px-3 py-1 rounded-sm text-sm border border-solid border-gray-500"
          type="password"
          name="password"
          id="login-password"
          autoComplete="current-password"
        />
      </div>
      <button
        className="bg-eki-blue px-3 py-1 lg:col-start-2 lg:ml-2 rounded-md text-white w-max"
        type="submit"
      >
        Sisene
      </button>
    </form>
  );
}
