import { CookieKeys } from "@/lib/enums/cookie-keys.enum";
import { Paths } from "@/lib/enums/paths.enum";
import { NextRequest, NextResponse } from "next/server";

export const config = {
  matcher: [
    /*
     * Match all request paths except for the ones starting with:
     * - api (API routes)
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico, sitemap.xml, robots.txt (metadata files)
     */
    "/((?!api|_next/static|_next/image|favicon.ico|sitemap.xml|robots.txt).*)",
  ],
};

const unprotectedPaths = [Paths.LOGIN];

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;
  // Forward request if it's to an unprotected path or when the request has a session cookie
  if (
    unprotectedPaths.some((url) => pathname.startsWith(url)) ||
    request.cookies.get(CookieKeys.JSESSIONID)
  ) {
    return NextResponse.next();
  }
  return NextResponse.redirect(new URL(Paths.LOGIN));
}
