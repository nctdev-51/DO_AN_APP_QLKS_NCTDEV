/**
 * Package: iuh.fit.app
 * 
 * TẦNG: APP - Application Bootstrap Layer
 * 
 * TRÁCH NHIỆM:
 * - Entry point của ứng dụng (main method)
 * - Setup Dependency Injection (tạo instances và wire dependencies)
 * - Khởi tạo ứng dụng (initialize resources)
 * - Cleanup khi shutdown (close resources)
 * - Manage application lifecycle
 * 
 * NGUYÊN TẮC:
 * ✅ Singleton pattern cho resources (EMF, connection pools)
 * ✅ Centralized DI setup
 * ✅ Proper resource management (try-finally, close in finally)
 * ✅ Error handling for startup failures
 * ✅ Logging throughout lifecycle
 * ❌ KHÔNG business logic ở đây
 * ❌ KHÔNG data processing
 * ❌ KHÔNG complex computations
 * 
 * LIFECYCLE:
 * 
 * main() - Entry point
 *   ↓
 * launch() - Start JavaFX runtime
 *   ↓
 * start(Stage) - Called by JavaFX runtime
 *   ├─ Initialize JPA (JpaConfig)
 *   ├─ Setup Dependency Injection
 *   ├─ Show initial screen (LoginController)
 *   └─ Block until application close
 * 
 * Application.stop() - When user closes window
 *   ├─ Close JPA resources
 *   ├─ Close database connections
 *   └─ Clean up other resources
 * 
 * DEPENDENCY INJECTION PATTERN:
 * 
 * public class MainApp extends Application {
 * 
 *     // Step 1: Initialize JPA
 *     JpaConfig.getEntityManagerFactory();  // Singleton EMF
 * 
 *     // Step 2: Create Repositories (Infrastructure)
 *     ITaiKhoanRepository repo = new TaiKhoanRepositoryImpl();
 * 
 *     // Step 3: Create Services (Core)
 *     IAuthenticationService service = new AuthenticationServiceImpl(repo);
 * 
 *     // Step 4: Create Controllers (Presentation)
 *     LoginController controller = new LoginController(service);
 * 
 *     // Step 5: Show Scene
 *     Scene scene = controller.createLoginScene();
 *     primaryStage.setScene(scene);
 * }
 * 
 * FLOW DIAGRAM:
 * 
 * Application Startup:
 * ┌─────────────────────────┐
 * │ main(String[] args)     │
 * │   → launch(args)        │
 * └────────────┬────────────┘
 *              ↓
 * ┌─────────────────────────┐
 * │ JavaFX Runtime Start    │
 * │ → start(primaryStage)   │
 * └────────────┬────────────┘
 *              ↓
 * ┌─────────────────────────┐
 * │ Initialize Resources    │
 * │ ├─ JpaConfig setup      │
 * │ ├─ Create Repositories  │
 * │ ├─ Create Services      │
 * │ └─ Create Controllers   │
 * └────────────┬────────────┘
 *              ↓
 * ┌─────────────────────────┐
 * │ Show Initial Scene      │
 * │ (LoginController)       │
 * └────────────┬────────────┘
 *              ↓
 * ┌─────────────────────────┐
 * │ User Interaction Loop   │
 * │ (Application Running)   │
 * └────────────┬────────────┘
 *              ↓ (User closes window)
 * ┌─────────────────────────┐
 * │ Cleanup Resources       │
 * │ ├─ Close JPA            │
 * │ └─ Close connections    │
 * └─────────────────────────┘
 * 
 * CÁC FILES:
 * - MainApp.java
 *   ├─ main(String[] args): Entry point
 *   ├─ start(Stage): Initialize & show UI
 *   ├─ showLoginScreen(): Display login
 *   ├─ showMainScreen(): Navigate to main
 *   ├─ setupDependencyInjection(): Create instances
 *   ├─ handleWindowClose(): Cleanup
 *   └─ stop(): Shutdown handler
 * 
 * BEST PRACTICES:
 * 
 * 1. Lazy Initialization:
 *    - Create resources only when needed
 *    - Example: JpaConfig.getEntityManagerFactory()
 * 
 * 2. Resource Management:
 *    - Use try-finally to ensure cleanup
 *    - Log important lifecycle events
 * 
 * 3. Error Handling:
 *    - Log startup errors
 *    - Show user-friendly messages
 *    - Exit gracefully if startup fails
 * 
 * 4. Configuration:
 *    - Externalize configuration (database URL, port, etc.)
 *    - Load from environment or config files
 * 
 * @author Clean Architecture Expert
 * @version 1.0
 */
package iuh.fit.app;

