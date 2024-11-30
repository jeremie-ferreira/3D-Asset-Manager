# 3D Asset Manager

## Overview
Organize your 3D assets, optimize search and archiving workflows, and accelerate your team's projects. This application offers an intuitive platform for managing and visualizing 3D content, ensuring efficient collaboration and streamlined workflows. Built with modern web technologies, it empowers teams to focus on creativity and productivity.

## Features
- **3D Asset Management**: Seamlessly upload, organize, and visualize 3D models, add all the necessary sources for future development.
- **Advanced Search**: Search by categories, tags, and metadata to find assets quickly.
- **3D Model Viewer**: Visualize 3D models directly in the application, take screenshots of your model.
- **RESTful API Integration**: Fetch and update data dynamically from the backend.
- **Secure Backend**: Robust backend with authentication and user management.
- **Responsive Design**: Fully responsive UI for desktop and mobile devices.

## Technologies Used
### Frontend
- **Framework**: Angular 18.2.10
- **UI Library**: Angular Material 18.2.10
- **3D Rendering**: THREE.js
- **API Communication**: RxJS and HttpClient
- **Styling**: SCSS
- **Dependency Management**: Node.js and npm

### Backend
- **Java 17**
- **SpringBoot**: 3.1.12 (Spring Data JPA, Hibernate, Spring Security)
- **Database**: PostgreSQL 14

## Prerequisites
Before running the application, ensure you have the following installed:

### Frontend
- [Node.js](https://nodejs.org/) (v16 or higher recommended)
- npm (comes with Node.js)

### Backend
- Java 17
- Maven
- PostgreSQL 14

## Repository strucutre
## Folder Structure
<pre>
root
├── backend/
│   └── assets-manager/
│       ├── src/
│       └── pom.xml/
└── frontend/
    ├── model-manager/
    └── package.json/
</pre>

## Installation
### Backend
1. Clone the repository:
<pre>
git clone <repository-url> 
cd <repository-folder>/backend  
</pre>
2. Configure properties database properties
Update application.properties with your PostgreSQL credentials. Example:
<pre>
spring.datasource.url=jdbc:postgresql://localhost:5432/assets-manager
spring.datasource.username=postgres-username
spring.datasource.password=postgres-password
</pre>
3. Configure storage folder
Update custom.properties (next to application.properties) with your storage folder. Example:
<pre>
files.location=C:/path/to/storage
</pre>
4. Run the backend:
<pre>
./mvnw spring-boot:run
</pre>

### Frontend
1. Clone the repository:
<pre>
   git clone https://github.com/your-repo/angular-app.git
   cd angular-app
</pre>
2. Install dependencies:
<pre>
   npm install
</pre>
4. Start the development server
<pre>
ng serve --o
</pre>

## Building the Application
### Backend
<pre>
./mvnw clean install  
</pre>

### Frontend
To build the application for production:
<pre>
   ng build --prod
</pre>
The compiled files will be in the `dist/` directory.

## License
[MIT License](LICENSE)

