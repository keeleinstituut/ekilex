"use server";

import { cookies } from "next/headers";
import { UserFilter } from "../models/permissions/permissions";

export async function searchPermissions(searchParams: {
  [key: string]: string;
}): Promise<UserFilter> {
  const sessionId = cookies().get("JSESSIONID");
  const paramsString = new URLSearchParams(searchParams).toString();
  return await fetch(
    `${process.env.API_URL}/proto/permissions/search?${paramsString}`,
    {
      method: "POST",
      headers: {
        Accept: "application/json",
        // Include user's session
        cookie: `JSESSIONID=${sessionId?.value}`,
      },
      redirect: "manual",
    }
  )
    .then((res) => {
      return res.json();
    })
    .then((res) => res);
}
