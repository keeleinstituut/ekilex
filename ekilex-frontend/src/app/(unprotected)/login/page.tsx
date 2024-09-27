import { Paths } from "@/lib/enums/paths.enum";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import LoginForm from "./login-form";

export default async function Page() {
  if (cookies().get("JSESSIONID")) {
    redirect(Paths.INDEX);
  }
  return (
    <section className="shadow-md rounded-sm overflow-hidden">
      <h1 className="bg-gray-100 px-5 pt-1 pb-4 text-gray-600 border-b border-gray-300 border-solid w-full">
        Logi sisse
      </h1>
      <LoginForm />
    </section>
  );
}
