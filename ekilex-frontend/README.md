# Requirements
Nodejs 18+
Either an .env* file containing API_URL or that same variable already defined in your environment  
For example you can have a file called ```.env.local``` containing ```API_URL=http://localhost:5555```

## Dev setup
1. Start ekilex-app by following the instructions in ekilex-app/README.md
2. Open up a new terminal and move into the ekilex-frontend folder
3. Run npm i
4. Run npm run dev

## Running prod build (assumes ekilex-app is already running)
1. Make sure you've added either an .env* file or added the variable into your environment that points to ekilex-app
2. Move into the ekilex-frontend folder in your terminal/script
3. Run npm ci
4. Run npm build
5. Run npm run start


## Technologies
This project uses Next.js and React along with Tailwind