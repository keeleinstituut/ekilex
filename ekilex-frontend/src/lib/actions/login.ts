"use server";

import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { Paths } from "../enums/paths.enum";
import { CookieKeys } from "../enums/cookie-keys.enum";

export async function login(formData: unknown): Promise<void> {
  await fetch(`${process.env.API_URL}/dologin`, {
    method: "POST",
    body: new URLSearchParams(formData as URLSearchParams),
    // Spring will redirect you upon login by default,
    // which changes the response for us and prevents us from getting the cookie
    redirect: "manual",
  }).then((res) => {
    // Retrieve the "Set-Cookie" header and jsessionid from it, which is what spring uses
    const sessionId = res.headers
      .getSetCookie()
      .find((cookie) => cookie.includes(CookieKeys.JSESSIONID));
    if (sessionId) {
      // Get the value and path strings
      const [value, path] = sessionId
        .split(";")
        .map((string) =>
          string.includes("=") ? string.split("=")[1] : string
        );
      if (value) {
        // Set the cookie for the user
        cookies().set(CookieKeys.JSESSIONID, value, { path, httpOnly: true });
        redirect(Paths.INDEX);
      }
    }
  });
}
