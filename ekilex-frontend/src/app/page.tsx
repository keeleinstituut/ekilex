import { cookies } from "next/headers";
import Link from "next/link";

export default function Home() {
  const userCookies = cookies();
  if (userCookies.get("JSESSIONID")) {
    return (
      <Link href="/permissions" className="text-eki-blue">
        Kasutajaõiguste haldus
      </Link>
    );
  }
  return (
    <Link
      href="/login"
      className="bg-eki-blue px-3 py-1 lg:col-start-2 lg:ml-2 rounded-md text-white w-max"
    >
      Logi sisse
    </Link>
  );
}
