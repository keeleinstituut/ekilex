"use server";

import { cookies } from "next/headers";
import { UserFilter } from "../models/permissions/permissions";
import { redirect } from "next/navigation";
import { CookieKeys } from "../enums/cookie-keys.enum";

export async function searchPermissions(searchParams: {
  [key: string]: string;
}): Promise<UserFilter> {
  const sessionId = cookies().get(CookieKeys.JSESSIONID);
  const paramsString = new URLSearchParams(searchParams).toString();
  return await fetch(
    `${process.env.API_URL}/proto/permissions/search?${paramsString}`,
    {
      method: "POST",
      headers: {
        Accept: "application/json",
        // Include user's session
        cookie: `${CookieKeys.JSESSIONID}=${sessionId?.value}`,
      },
      redirect: "error",
    }
  )
    .then((res) => {
      return res.json();
    })
    .then((res) => res)
    .catch(() => {
      // If we got an error it likely means our session expired
      redirect("/login");
    });
}
