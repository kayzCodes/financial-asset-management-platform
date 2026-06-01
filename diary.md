# 📘 Development Diary

### 📅 01/10/25

- Met with my tutor to discuss project and plan details

### 📅 02/10/25 - 10/10/2025

- I was working on my project plan
- I sent a draft to my tutur and awaited feedback when i recoved it i made the corresponding adjustments.
- I submitted it on the 9th of october

### 📅 13/10/25 - 19/10/2025

- Set up my repository like listed on gitlab and commited it

### 📅 20/10/25 - 26/10/2025

- Designed my frontend and made a database draft and added them to my documents folder so i can start coding
- I have set up my spring boot backend
- I have set up my react frontend
- I have created my gitignore and my gitlab-ci
- I met with my supervisor and was told to add a login page with more frontend design

### 📅 27/10/25 - 2/11/2025

- I set up google checkstyle and jacoco test coverage at 90%
- I set up spotbugs
- I set up my pipe line so it works with maven
- I cleaned up the repo so there was no unnecessary boiler plate or unused code

### 📅 3/11/25 - 9/11/2025

- This work i worked on making an actual live backend which consited of:
- I created failing tests for all the files mentioned below in order to ahere to TDD (Test Driven Development)
- I created the entity classes
- I created the Data Transfer Object
- I created the mapper to go from entity to Data Transfer Object and vice versa
- I created the repository for each entity so that they have crud functions
- I made the frontend folders such as pages, component and section.

### 📅 10/11/25 - 16/11/2025

- Created failing Controller and service implemenrtation tests as per TDD (Test Driven Development)
- I created a service interface
- I created a service interface implementation
- I Created a controller that uses the service methods
- I met with my tutor and was told to create a decent ui and have full authentication.

### 📅 17/11/25 - 23/11/2025

- I wrote failing tests for the for the config files
- I created the security config to allow a temporary password hashing so a user can log in and access the dashboard
- I built the frontend pages for when a user is not logged in such as:
- Made a home page with a nav bar that has an about link features link pricing link and contact link which all direct you to a page with dummy information
- i also have a dashboard page for when you log in:
- you have a personalised dashboard that has a link to your tailored news and the users goals on the sidebar is has your assets and your portfolio overview.
- i have created authentication using firebase that gives protected routes and only allows users to specific pages only if the are authenticated.
- I have added a firebase token filter to the backend so there is security when a user goes from frontend to backend using the firebase token.
- i met with my supervisro and she told me to have a full ui and start the report.

### 📅 24/11/25 - 30/11/2025

- I had a microsoft teams meeting with my tutor
- I made a template for my interim report
- When a user is not logged i made a marketing page that has a full ui functionaloity
- In the marketing page there is an about page, features page, pricing page and contact page
- When a user is logged in there is a full ui
- There is a settings and account page
- There is a portfolio overview with a chart of how much your total wealth is
- There is a stocks and crypto page where you can add your stocks and see the chart of its information
- There is a news page where you can see the relevant current affairs
- There is a goals page where you can add your financial goals and see your progress.
- I sent a draft of my report to my supervisor
- My supervisor gave me feedback

### 📅 01/12/25 - 07/12/2025

- I continuted with my intermin report
- I connected the backend with the frontend so you can actually add stocks crypto and goals
- I also created gets so they can be returned to the frontend as a list
- the list is also clickable so you can see it in depth
- I had my presentation
- I am still working on my intermin report

### 📅 08/12/25 - 15/12/2025

- I made the stock page and crypto responsive you can add stocks and crypto with end to end functionality with the database
- On the stock and crypto page if you click on the added asset you will see all the details of the asset on the right with mocked chart data and percentegs for now
- You can now add gaols as well which has end to end functionality with the backend
- I had my presentaion
- I am still working on my intermin report

### 📅 19/01/2026 - 26/01/2026

- Completed remaining backend CRUD functionality for stocks, crypto, and financial goals.
- Implemented edit and delete operations with proper ownership validation.
- Separated buy and sell logic into distinct endpoints for financial correctness.
- Ensured weighted average recalculation on buy and no average change on sell.
- Added unit tests for transaction flows and validation rules.
- Began refining service-layer architecture for clean separation of concerns.

### 📅 26/01/2026 - 01/02/2026

- Integrated live price data for stocks and crypto via backend aggregation.
- Implemented chart endpoint supporting 1D, 1W, 1M, and 1Y ranges.
- Added backend slicing logic for time-series data (intraday vs daily).
- Implemented caching strategy to prevent rate-limit issues.
- Updated frontend portfolio views to display live current value and percentage change.
- Enhanced asset detail pages with real-time price and calculated performance metrics.

### 📅 02/02/2026 - 08/02/2026

- Finalised chart visualisation and improved UI responsiveness.
- Implemented derived metric calculations (currentValue, percentageChange) server-side.
- Added key statistics and company overview integration.
- Improved frontend layout consistency between stock and crypto pages.
- Conducted performance testing on live data endpoints.

### 📅 09/02/2026 - 15/02/2026

- Researched artificial intelligence techniques for portfolio-based financial news analysis.
- Investigated financial news APIs and sentiment analysis capabilities.
- Designed unified internal NewsArticle model for provider abstraction.
- Planned architecture for AI-generated portfolio news digest.
- Began drafting sections of final year project report (system architecture + data flow).

### 📅 16/02/2026 - 22/02/2026

- Implemented personalised AI-driven news feature based on tracked assets.
- Developed backend service to fetch, filter, rank, and summarise news articles.
- Integrated sentiment scoring and relevance filtering.
- Implemented caching and scheduled refresh logic for news digests.
- Exposed secured GET /api/news/digest endpoint.
- Continued working on report documentation (design decisions + justification).

### 📅 23/02/2026 - 01/03/2026

- Finalised integration of AI-generated portfolio news with frontend.
- Conducted end-to-end testing across stocks, crypto, and news systems.
- Optimised caching and refresh behaviour.
- Refined UI presentation of AI summaries and related sources.
- Continued writing final report sections (evaluation, implementation details, reflection).
- Began preparing system for final submission and demonstration readiness.

### 📅 02/03/2026 - 08/02/2026

- Implemented portfolio overview backend aggregation endpoint with GBP normalisation.
- Integrated transaction-based cost basis calculation and live valuation logic.
- Developed portfolio chart generation using aggregated time-series data.
- Implemented asset allocation calculations (stocks vs crypto and per-asset split).
- Added top and worst performer logic based on percentage gain/loss.
- Applied caching strategy for portfolio overview and chart data to improve performance.
- Began frontend implementation of portfolio overview components (chart, PnL, allocation).

### 📅 09/03/2026 - 15/02/2026

- Completed frontend portfolio overview page including chart, PnL breakdown, and asset list.
- Implemented pie chart visualisation for portfolio allocation.
- Integrated top and worst performer UI components.
- Added manual refresh functionality with cooldown and loading states.
- Conducted end-to-end testing across portfolio, stocks, and crypto features.
- Refined UI/UX for responsiveness and improved accessibility across devices.
- Continued final year report writing (implementation details, evaluation, and system design).
- Performed final optimisations and prepared system for submission and demonstration.

### 📅 16/03/2026 - 22/03/2026

- Completed full portfolio overview implementation, including chart aggregation, PnL calculations, and asset allocation.
- Integrated transaction-based portfolio modelling and ensured accurate cost basis and valuation logic.
- Implemented caching strategies for portfolio data and optimised backend performance.
- Continued refining frontend UI components for portfolio display and data visualisation.

### 📅 23/03/2026 - 29/02/2026

- Met with my supervisor to review progress and discuss improvements for the final stage of the project.
- Implemented AI-driven personalised news system with filtering, ranking, and caching of articles.
- Continued development of portfolio features, improving data consistency between frontend and backend.
- Began writing the final report and structuring sections based on feedback and marking criteria.

### 📅 30/03/2026 - 05/04/2026

- Focused on writing and refining the final report, including critical analysis and conclusion sections.
- Cleaned up backend and frontend code, improving structure, readability, and maintainability.
- Performed end-to-end testing across portfolio, assets, and news features to ensure system stability.
- Made final UI adjustments and prepared the system for demonstration and submission.
