import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        "eki-blue": "#215da5",
        "eki-warning": "#ffc107",
        "eki-success": "#00892e",
        "eki-gray-100": "#ecf0f2",
        "eki-gray-200": "#ccd9e0",
        "eki-gray-400": "#5d606e",
      },
    },
  },
  plugins: [],
};
export default config;
