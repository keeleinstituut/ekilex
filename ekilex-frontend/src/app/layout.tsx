import type { Metadata } from "next";
import "./globals.css";
import { Header } from "@/components/header/header";

export const metadata: Metadata = {
  title: "Ekilex",
  description: "Ekilex",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <Header />
        <main className="p-3">{children}</main>
      </body>
    </html>
  );
}
