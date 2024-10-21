## EKI components
This project is meant to be used as a way to generate shared web-components, allowing you to use the bundled files in ekilex/wordweb

## Technologies
Svelte  
Vite  
Typescript  

## How to use
1. Develop your components using npm run dev (You'll have to rebuild web components and restart dev on changes, so quick development should use the svelte component import instead)  
2. Import the finished components into library.ts
3. Run npm run build  
4. Copy the resulting file from eki-components/eki-components.js into whereever you need to use the web components  
5. Use the components in the DOM as intended  