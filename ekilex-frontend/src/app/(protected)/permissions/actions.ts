"use server";

import { logout } from "@/lib/actions/session";
import { ApiEndpoints } from "@/lib/enums/api-endpoints.enum";
import { CookieKeys } from "@/lib/enums/cookie-keys.enum";
import { Paths } from "@/lib/enums/paths.enum";
import {
  UserFilter,
  UserPermissionsInit,
  UserPermissionType,
} from "@/lib/models/permissions/permissions";
import { unstable_cache } from "next/cache";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";

// Cache user permissions for an hour
export const getPermissions = unstable_cache(
  async (sessionId: string) => {
    return await fetch(`${process.env.API_URL}${ApiEndpoints.PERMISSIONS}`, {
      headers: {
        Accept: "application/json",
        // Include user's session
        cookie: `${CookieKeys.JSESSIONID}=${sessionId}`,
      },
      redirect: "error",
      method: "POST",
    })
      .then((res) => res.json())
      .then((res: UserPermissionsInit) => res)
      .catch(() => {
        console.error(`
          Failed to fetch permissions at ${new Date()} for sessionId: ${sessionId}`);
        redirect(Paths.LOGIN);
      });
  },
  ["user-permissions"],
  { revalidate: 3600 }
);

export async function searchPermissions(
  searchParams: {
    [key: string]: string;
  },
  sessionId: string
): Promise<UserFilter> {
  const paramsString = new URLSearchParams(searchParams).toString();
  return await fetch(
    `${process.env.API_URL}${ApiEndpoints.PERMISSIONS_SEARCH}?${paramsString}`,
    {
      method: "POST",
      headers: {
        Accept: "application/json",
        // Include user's session
        cookie: `${CookieKeys.JSESSIONID}=${sessionId}`,
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
      logout();
    });
}

export async function setPermission(
  userId: number,
  type: UserPermissionType,
  newValue: boolean
): Promise<boolean> {
  const sessionCookie = cookies().get(CookieKeys.JSESSIONID);
  // If there's no cookie, something is clearly wrong
  if (!sessionCookie) {
    logout();
    return !newValue;
  }
  let endpoint: string;
  switch (type) {
    case "admin":
      endpoint = newValue
        ? ApiEndpoints.PERMISSIONS_SET_ADMIN
        : ApiEndpoints.PERMISSIONS_REMOVE_ADMIN;
      break;
    case "crud":
      endpoint = newValue
        ? ApiEndpoints.PERMISSIONS_SET_API_CRUD
        : ApiEndpoints.PERMISSIONS_REMOVE_API_CRUD;
      break;
    case "master":
      endpoint = newValue
        ? ApiEndpoints.PERMISSIONS_SET_MASTER
        : ApiEndpoints.PERMISSIONS_REMOVE_MASTER;
      break;
    case "enabled":
      endpoint = newValue
        ? ApiEndpoints.PERMISSIONS_ENABLE_ACCOUNT
        : ApiEndpoints.PERMISSIONS_DISABLE_ACCOUNT;
      break;
    default:
      return !newValue;
  }
  const body = new URLSearchParams([["userId", `${userId}`]]);
  return await fetch(`${process.env.API_URL}${endpoint}`, {
    method: "POST",
    headers: {
      cookie: `${CookieKeys.JSESSIONID}=${sessionCookie?.value}`,
    },
    body,
    redirect: "error",
  })
    .then((res) => {
      if (res.status === 200) {
        return newValue;
      }
      console.error(`
        Failed to set permission ${type} for ${userId}, status: ${res.status}`);
      return !newValue;
    })
    .catch((e) => {
      console.error(`
        Failed to set permission ${type} for ${userId}, error: ${e}`);
      return !newValue;
    });
}
