package com.hangman;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class GameLogic {
    private static final String[] WORDS = {
        // Programming Languages & Frameworks
        "JAVA", "PYTHON", "JAVASCRIPT", "TYPESCRIPT", "C_PLUS_PLUS", "C_SHARP", "RUST", "GO", "KOTLIN", "SWIFT",
        "JAVAFX", "SPRING", "REACT", "ANGULAR", "VUE", "NODEJS", "EXPRESS", "DJANGO", "FLASK", "LARAVEL",
        
        // Computer Science Concepts
        "ALGORITHM", "DATASTRUCTURE", "COMPLEXITY", "RECURSION", "ITERATION", "SORTING", "SEARCHING", "GRAPH", "TREE", "HEAP",
        "QUEUE", "STACK", "LINKEDLIST", "ARRAY", "HASHTABLE", "BINARYTREE", "AVLTREE", "REDBLACKTREE", "BPLUSTREE",
        
        // Software Engineering
        "ARCHITECTURE", "DESIGNPATTERN", "SOLID", "DRY", "KISS", "AGILE", "SCRUM", "KANBAN", "WATERFALL", "DEVOPS",
        "CI_CD", "MICROSERVICES", "MONOLITH", "API", "REST", "GRAPHQL", "SOAP", "WEBSOCKET", "GRPC",
        
        // Database & Data
        "DATABASE", "SQL", "NOSQL", "MONGODB", "POSTGRESQL", "MYSQL", "REDIS", "CASSANDRA", "ELASTICSEARCH", "KAFKA",
        "DATAWAREHOUSE", "DATALAKE", "ETL", "OLTP", "OLAP", "NORMALIZATION", "INDEXING", "TRANSACTION", "ACID",
        
        // Web Technologies
        "HTML", "CSS", "HTTP", "HTTPS", "DNS", "CDN", "SSL", "TLS", "JWT", "OAUTH", "CORS", "CSP", "SEO", "PWA",
        
        // Cloud & Infrastructure
        "CLOUD", "AWS", "AZURE", "GCP", "DOCKER", "KUBERNETES", "TERRAFORM", "ANSIBLE", "JENKINS", "GITHUB",
        "VIRTUALIZATION", "CONTAINERIZATION", "ORCHESTRATION", "SCALABILITY", "LOADBALANCING", "AUTOSCALING",
        
        // Artificial Intelligence & ML
        "MACHINELEARNING", "DEEPLEARNING", "NEURALNETWORK", "TENSORFLOW", "PYTORCH", "SCIKIT", "REINFORCEMENT", "SUPERVISED",
        "UNSUPERVISED", "CLASSIFICATION", "REGRESSION", "CLUSTERING", "NATURALLANGUAGE", "COMPUTERVISION", "ROBOTICS",
        
        // Cybersecurity
        "ENCRYPTION", "HASHING", "AUTHENTICATION", "AUTHORIZATION", "PENETRATION", "VULNERABILITY", "MALWARE", "PHISHING",
        "FIREWALL", "INTRUSION", "DETECTION", "PREVENTION", "SOC", "SIEM", "ZERO_TRUST", "MULTIFACTOR",
        
        // Computer Architecture
        "PROCESSOR", "MEMORY", "CACHE", "BUS", "REGISTER", "PIPELINE", "MULTICORE", "HYPERTHREADING", "VIRTUALMEMORY",
        "PAGING", "FRAGMENTATION", "BUFFER", "INTERRUPT", "DMA", "RAID", "SSD", "HDD",
        
        // Operating Systems
        "OPERATINGSYSTEM", "KERNEL", "PROCESS", "THREAD", "SCHEDULING", "SYNCHRONIZATION", "DEADLOCK", "RACECONDITION",
        "SEMAPHORE", "MUTEX", "MONITOR", "SIGNAL", "FORK", "EXEC", "PIPE", "SOCKET",
        
        // Networks & Communication
        "NETWORK", "PROTOCOL", "TCP", "UDP", "IP", "ROUTING", "SWITCHING", "BANDWIDTH", "LATENCY", "THROUGHPUT",
        "PACKET", "FRAME", "ETHERNET", "WIFI", "BLUETOOTH", "5G", "FIBER", "COAXIAL",
        
        // Academic & Research
        "COMPILER", "INTERPRETER", "LEXER", "PARSER", "OPTIMIZATION", "COMPLEXITY", "THEORY", "COMPUTABILITY", "TURING",
        "CHURCH", "P_NP", "NP_COMPLETE", "ALGORITHM", "HEURISTIC", "METAHEURISTIC", "GENETIC", "EVOLUTIONARY"
    };
    

    
    // Word-Category mapping for logical categorization
    private static final Map<String, String> WORD_CATEGORIES = new HashMap<>();
    
    static {
        // Programming Languages
        WORD_CATEGORIES.put("JAVA", "Programming Languages");
        WORD_CATEGORIES.put("PYTHON", "Programming Languages");
        WORD_CATEGORIES.put("JAVASCRIPT", "Programming Languages");
        WORD_CATEGORIES.put("C_PLUS_PLUS", "Programming Languages");
        WORD_CATEGORIES.put("C_SHARP", "Programming Languages");
        WORD_CATEGORIES.put("RUBY", "Programming Languages");
        WORD_CATEGORIES.put("GO", "Programming Languages");
        WORD_CATEGORIES.put("RUST", "Programming Languages");
        WORD_CATEGORIES.put("SWIFT", "Programming Languages");
        WORD_CATEGORIES.put("KOTLIN", "Programming Languages");
        WORD_CATEGORIES.put("PHP", "Programming Languages");
        WORD_CATEGORIES.put("SCALA", "Programming Languages");
        WORD_CATEGORIES.put("HASKELL", "Programming Languages");
        WORD_CATEGORIES.put("ELIXIR", "Programming Languages");
        WORD_CATEGORIES.put("CLOJURE", "Programming Languages");
        WORD_CATEGORIES.put("ERLANG", "Programming Languages");
        WORD_CATEGORIES.put("F_SHARP", "Programming Languages");
        WORD_CATEGORIES.put("DART", "Programming Languages");
        WORD_CATEGORIES.put("PERL", "Programming Languages");
        WORD_CATEGORIES.put("LUA", "Programming Languages");
        
        // Web Development
        WORD_CATEGORIES.put("HTML", "Web Development");
        WORD_CATEGORIES.put("CSS", "Web Development");
        WORD_CATEGORIES.put("HTTP", "Web Development");
        WORD_CATEGORIES.put("HTTPS", "Web Development");
        WORD_CATEGORIES.put("DNS", "Web Development");
        WORD_CATEGORIES.put("CDN", "Web Development");
        WORD_CATEGORIES.put("SSL", "Web Development");
        WORD_CATEGORIES.put("TLS", "Web Development");
        WORD_CATEGORIES.put("JWT", "Web Development");
        WORD_CATEGORIES.put("OAUTH", "Web Development");
        WORD_CATEGORIES.put("CORS", "Web Development");
        WORD_CATEGORIES.put("CSP", "Web Development");
        WORD_CATEGORIES.put("SEO", "Web Development");
        WORD_CATEGORIES.put("PWA", "Web Development");
        WORD_CATEGORIES.put("REST", "Web Development");
        WORD_CATEGORIES.put("GRAPHQL", "Web Development");
        WORD_CATEGORIES.put("SOAP", "Web Development");
        WORD_CATEGORIES.put("WEBSOCKET", "Web Development");
        WORD_CATEGORIES.put("GRPC", "Web Development");
        
        // Database Systems
        WORD_CATEGORIES.put("DATABASE", "Database Systems");
        WORD_CATEGORIES.put("SQL", "Database Systems");
        WORD_CATEGORIES.put("NOSQL", "Database Systems");
        WORD_CATEGORIES.put("MONGODB", "Database Systems");
        WORD_CATEGORIES.put("POSTGRESQL", "Database Systems");
        WORD_CATEGORIES.put("MYSQL", "Database Systems");
        WORD_CATEGORIES.put("REDIS", "Database Systems");
        WORD_CATEGORIES.put("CASSANDRA", "Database Systems");
        WORD_CATEGORIES.put("ELASTICSEARCH", "Database Systems");
        WORD_CATEGORIES.put("KAFKA", "Database Systems");
        WORD_CATEGORIES.put("DATAWAREHOUSE", "Database Systems");
        WORD_CATEGORIES.put("DATALAKE", "Database Systems");
        WORD_CATEGORIES.put("ETL", "Database Systems");
        WORD_CATEGORIES.put("OLTP", "Database Systems");
        WORD_CATEGORIES.put("OLAP", "Database Systems");
        WORD_CATEGORIES.put("NORMALIZATION", "Database Systems");
        WORD_CATEGORIES.put("INDEXING", "Database Systems");
        WORD_CATEGORIES.put("TRANSACTION", "Database Systems");
        WORD_CATEGORIES.put("ACID", "Database Systems");
        
        // Cloud Computing
        WORD_CATEGORIES.put("CLOUD", "Cloud Computing");
        WORD_CATEGORIES.put("AWS", "Cloud Computing");
        WORD_CATEGORIES.put("AZURE", "Cloud Computing");
        WORD_CATEGORIES.put("GCP", "Cloud Computing");
        WORD_CATEGORIES.put("DOCKER", "Cloud Computing");
        WORD_CATEGORIES.put("KUBERNETES", "Cloud Computing");
        WORD_CATEGORIES.put("TERRAFORM", "Cloud Computing");
        WORD_CATEGORIES.put("ANSIBLE", "Cloud Computing");
        WORD_CATEGORIES.put("JENKINS", "Cloud Computing");
        WORD_CATEGORIES.put("GITHUB", "Cloud Computing");
        WORD_CATEGORIES.put("VIRTUALIZATION", "Cloud Computing");
        WORD_CATEGORIES.put("CONTAINERIZATION", "Cloud Computing");
        WORD_CATEGORIES.put("ORCHESTRATION", "Cloud Computing");
        WORD_CATEGORIES.put("SCALABILITY", "Cloud Computing");
        WORD_CATEGORIES.put("LOADBALANCING", "Cloud Computing");
        WORD_CATEGORIES.put("AUTOSCALING", "Cloud Computing");
        
        // Artificial Intelligence & Machine Learning
        WORD_CATEGORIES.put("MACHINELEARNING", "Machine Learning");
        WORD_CATEGORIES.put("DEEPLEARNING", "Machine Learning");
        WORD_CATEGORIES.put("NEURALNETWORK", "Machine Learning");
        WORD_CATEGORIES.put("TENSORFLOW", "Machine Learning");
        WORD_CATEGORIES.put("PYTORCH", "Machine Learning");
        WORD_CATEGORIES.put("SCIKIT", "Machine Learning");
        WORD_CATEGORIES.put("REINFORCEMENT", "Machine Learning");
        WORD_CATEGORIES.put("SUPERVISED", "Machine Learning");
        WORD_CATEGORIES.put("UNSUPERVISED", "Machine Learning");
        WORD_CATEGORIES.put("CLASSIFICATION", "Machine Learning");
        WORD_CATEGORIES.put("REGRESSION", "Machine Learning");
        WORD_CATEGORIES.put("CLUSTERING", "Machine Learning");
        WORD_CATEGORIES.put("NATURALLANGUAGE", "Machine Learning");
        WORD_CATEGORIES.put("COMPUTERVISION", "Machine Learning");
        WORD_CATEGORIES.put("ROBOTICS", "Machine Learning");
        
        // Cybersecurity
        WORD_CATEGORIES.put("ENCRYPTION", "Cybersecurity");
        WORD_CATEGORIES.put("HASHING", "Cybersecurity");
        WORD_CATEGORIES.put("AUTHENTICATION", "Cybersecurity");
        WORD_CATEGORIES.put("AUTHORIZATION", "Cybersecurity");
        WORD_CATEGORIES.put("PENETRATION", "Cybersecurity");
        WORD_CATEGORIES.put("VULNERABILITY", "Cybersecurity");
        WORD_CATEGORIES.put("MALWARE", "Cybersecurity");
        WORD_CATEGORIES.put("PHISHING", "Cybersecurity");
        WORD_CATEGORIES.put("FIREWALL", "Cybersecurity");
        WORD_CATEGORIES.put("INTRUSION", "Cybersecurity");
        WORD_CATEGORIES.put("DETECTION", "Cybersecurity");
        WORD_CATEGORIES.put("PREVENTION", "Cybersecurity");
        WORD_CATEGORIES.put("SOC", "Cybersecurity");
        WORD_CATEGORIES.put("SIEM", "Cybersecurity");
        WORD_CATEGORIES.put("ZERO_TRUST", "Cybersecurity");
        WORD_CATEGORIES.put("MULTIFACTOR", "Cybersecurity");
        
        // Computer Architecture
        WORD_CATEGORIES.put("PROCESSOR", "Computer Architecture");
        WORD_CATEGORIES.put("MEMORY", "Computer Architecture");
        WORD_CATEGORIES.put("CACHE", "Computer Architecture");
        WORD_CATEGORIES.put("BUS", "Computer Architecture");
        WORD_CATEGORIES.put("REGISTER", "Computer Architecture");
        WORD_CATEGORIES.put("PIPELINE", "Computer Architecture");
        WORD_CATEGORIES.put("MULTICORE", "Computer Architecture");
        WORD_CATEGORIES.put("HYPERTHREADING", "Computer Architecture");
        WORD_CATEGORIES.put("VIRTUALMEMORY", "Computer Architecture");
        WORD_CATEGORIES.put("PAGING", "Computer Architecture");
        WORD_CATEGORIES.put("FRAGMENTATION", "Computer Architecture");
        WORD_CATEGORIES.put("BUFFER", "Computer Architecture");
        WORD_CATEGORIES.put("INTERRUPT", "Computer Architecture");
        WORD_CATEGORIES.put("DMA", "Computer Architecture");
        WORD_CATEGORIES.put("RAID", "Computer Architecture");
        WORD_CATEGORIES.put("SSD", "Computer Architecture");
        WORD_CATEGORIES.put("HDD", "Computer Architecture");
        
        // Operating Systems
        WORD_CATEGORIES.put("OPERATINGSYSTEM", "Operating Systems");
        WORD_CATEGORIES.put("KERNEL", "Operating Systems");
        WORD_CATEGORIES.put("PROCESS", "Operating Systems");
        WORD_CATEGORIES.put("THREAD", "Operating Systems");
        WORD_CATEGORIES.put("SCHEDULING", "Operating Systems");
        WORD_CATEGORIES.put("SYNCHRONIZATION", "Operating Systems");
        WORD_CATEGORIES.put("DEADLOCK", "Operating Systems");
        WORD_CATEGORIES.put("RACECONDITION", "Operating Systems");
        WORD_CATEGORIES.put("SEMAPHORE", "Operating Systems");
        WORD_CATEGORIES.put("MUTEX", "Operating Systems");
        WORD_CATEGORIES.put("MONITOR", "Operating Systems");
        WORD_CATEGORIES.put("SIGNAL", "Operating Systems");
        WORD_CATEGORIES.put("FORK", "Operating Systems");
        WORD_CATEGORIES.put("EXEC", "Operating Systems");
        WORD_CATEGORIES.put("PIPE", "Operating Systems");
        WORD_CATEGORIES.put("SOCKET", "Operating Systems");
        
        // Computer Networks
        WORD_CATEGORIES.put("NETWORK", "Computer Networks");
        WORD_CATEGORIES.put("PROTOCOL", "Computer Networks");
        WORD_CATEGORIES.put("TCP", "Computer Networks");
        WORD_CATEGORIES.put("UDP", "Computer Networks");
        WORD_CATEGORIES.put("IP", "Computer Networks");
        WORD_CATEGORIES.put("ROUTING", "Computer Networks");
        WORD_CATEGORIES.put("SWITCHING", "Computer Networks");
        WORD_CATEGORIES.put("BANDWIDTH", "Computer Networks");
        WORD_CATEGORIES.put("LATENCY", "Computer Networks");
        WORD_CATEGORIES.put("THROUGHPUT", "Computer Networks");
        WORD_CATEGORIES.put("PACKET", "Computer Networks");
        WORD_CATEGORIES.put("FRAME", "Computer Networks");
        WORD_CATEGORIES.put("ETHERNET", "Computer Networks");
        WORD_CATEGORIES.put("WIFI", "Computer Networks");
        WORD_CATEGORIES.put("BLUETOOTH", "Computer Networks");
        WORD_CATEGORIES.put("5G", "Computer Networks");
        WORD_CATEGORIES.put("FIBER", "Computer Networks");
        WORD_CATEGORIES.put("COAXIAL", "Computer Networks");
        
        // Software Engineering
        WORD_CATEGORIES.put("ARCHITECTURE", "Software Engineering");
        WORD_CATEGORIES.put("DESIGNPATTERN", "Software Engineering");
        WORD_CATEGORIES.put("SOLID", "Software Engineering");
        WORD_CATEGORIES.put("DRY", "Software Engineering");
        WORD_CATEGORIES.put("KISS", "Software Engineering");
        WORD_CATEGORIES.put("AGILE", "Software Engineering");
        WORD_CATEGORIES.put("SCRUM", "Software Engineering");
        WORD_CATEGORIES.put("KANBAN", "Software Engineering");
        WORD_CATEGORIES.put("WATERFALL", "Software Engineering");
        WORD_CATEGORIES.put("DEVOPS", "Software Engineering");
        WORD_CATEGORIES.put("CI_CD", "Software Engineering");
        WORD_CATEGORIES.put("MICROSERVICES", "Software Engineering");
        WORD_CATEGORIES.put("MONOLITH", "Software Engineering");
        WORD_CATEGORIES.put("API", "Software Engineering");
        
        // Data Science
        WORD_CATEGORIES.put("DATAWAREHOUSE", "Data Science");
        WORD_CATEGORIES.put("DATALAKE", "Data Science");
        WORD_CATEGORIES.put("ETL", "Data Science");
        WORD_CATEGORIES.put("OLTP", "Data Science");
        WORD_CATEGORIES.put("OLAP", "Data Science");
        WORD_CATEGORIES.put("NORMALIZATION", "Data Science");
        WORD_CATEGORIES.put("INDEXING", "Data Science");
        WORD_CATEGORIES.put("TRANSACTION", "Data Science");
        WORD_CATEGORIES.put("ACID", "Data Science");
        
        // Academic Research
        WORD_CATEGORIES.put("COMPILER", "Academic Research");
        WORD_CATEGORIES.put("INTERPRETER", "Academic Research");
        WORD_CATEGORIES.put("LEXER", "Academic Research");
        WORD_CATEGORIES.put("PARSER", "Academic Research");
        WORD_CATEGORIES.put("OPTIMIZATION", "Academic Research");
        WORD_CATEGORIES.put("COMPLEXITY", "Academic Research");
        WORD_CATEGORIES.put("THEORY", "Academic Research");
        WORD_CATEGORIES.put("COMPUTABILITY", "Academic Research");
        WORD_CATEGORIES.put("TURING", "Academic Research");
        WORD_CATEGORIES.put("CHURCH", "Academic Research");
        WORD_CATEGORIES.put("P_NP", "Academic Research");
        WORD_CATEGORIES.put("NP_COMPLETE", "Academic Research");
        WORD_CATEGORIES.put("ALGORITHM", "Academic Research");
        WORD_CATEGORIES.put("HEURISTIC", "Academic Research");
        WORD_CATEGORIES.put("METAHEURISTIC", "Academic Research");
        WORD_CATEGORIES.put("GENETIC", "Academic Research");
        WORD_CATEGORIES.put("EVOLUTIONARY", "Academic Research");
    }
    
    private String currentWord;
    private String currentCategory;
    private Set<Character> guessedLetters;
    private int wrongGuesses;
    private int maxWrongGuesses;
    private int score;
    private int level;
    private boolean gameOver;
    private boolean gameWon;
    private int hintCount;
    private static final int MAX_HINTS = 3;
    
    private ExecutorService executorService;
    private Future<?> timerTask;
    private int timeRemaining;
    private static final int TIME_LIMIT = 60; // seconds
    private boolean isTimerPaused = false;
    private long pauseStartTime = 0;
    
    public GameLogic() {
        executorService = Executors.newSingleThreadExecutor();
        resetGame();
    }
    
    public void resetGame() {
        Random random = new Random();
        currentWord = WORDS[random.nextInt(WORDS.length)];
        
        // Use proper word-category mapping instead of random assignment
        currentCategory = WORD_CATEGORIES.getOrDefault(currentWord, "General Computing");
        
        guessedLetters = new HashSet<>();
        wrongGuesses = 0;
        maxWrongGuesses = 6;
        gameOver = false;
        gameWon = false;
        timeRemaining = TIME_LIMIT;
        hintCount = 0;
        startTimer();
    }
    
    private void startTimer() {
        if (timerTask != null && !timerTask.isDone()) {
            timerTask.cancel(true);
        }
        
        timerTask = executorService.submit(() -> {
            try {
                while (timeRemaining > 0 && !gameOver) {
                    Thread.sleep(1000);
                    timeRemaining--;
                    if (timeRemaining <= 0) {
                        gameOver = true;
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    public boolean makeGuess(char letter) {
        if (gameOver || guessedLetters.contains(letter)) {
            return false;
        }
        
        letter = Character.toUpperCase(letter);
        guessedLetters.add(letter);
        
        if (currentWord.indexOf(letter) == -1) {
            wrongGuesses++;
            if (wrongGuesses >= maxWrongGuesses) {
                gameOver = true;
            }
            return false;
        } else {
            checkWinCondition();
            return true;
        }
    }
    
    private void checkWinCondition() {
        for (char c : currentWord.toCharArray()) {
            if (!guessedLetters.contains(c)) {
                return;
            }
        }
        gameWon = true;
        gameOver = true;
        score += calculateScore();
        level++;
    }
    
    private int calculateScore() {
        int baseScore = 100;
        int timeBonus = timeRemaining * 2;
        int wrongGuessPenalty = wrongGuesses * 10;
        return Math.max(0, baseScore + timeBonus - wrongGuessPenalty);
    }
    
    public String getDisplayWord() {
        StringBuilder display = new StringBuilder();
        for (char c : currentWord.toCharArray()) {
            if (guessedLetters.contains(c)) {
                display.append(c);
            } else {
                display.append("_");
            }
            display.append(" ");
        }
        return display.toString().trim();
    }
    
    public String getCurrentWord() {
        return currentWord;
    }
    
    public String getCurrentCategory() {
        return currentCategory;
    }
    
    public Set<Character> getGuessedLetters() {
        return new HashSet<>(guessedLetters);
    }
    
    public int getWrongGuesses() {
        return wrongGuesses;
    }
    
    public int getMaxWrongGuesses() {
        return maxWrongGuesses;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getLevel() {
        return level;
    }
    
    public boolean isGameOver() {
        return gameOver;
    }
    
    public boolean isGameWon() {
        return gameWon;
    }
    
    public int getTimeRemaining() {
        return timeRemaining;
    }
    
    public int getHintCount() {
        return hintCount;
    }
    
    public int getMaxHints() {
        return MAX_HINTS;
    }
    
    public boolean canUseHint() {
        return hintCount < MAX_HINTS;
    }
    
    public void incrementHintCount() {
        if (hintCount < MAX_HINTS) {
            hintCount++;
        }
    }
    
    public void pauseTimer() {
        isTimerPaused = true;
        pauseStartTime = System.currentTimeMillis();
        if (timerTask != null && !timerTask.isDone()) {
            timerTask.cancel(true);
        }
    }
    
    public void resumeTimer() {
        if (isTimerPaused) {
            isTimerPaused = false;
            long pauseDuration = System.currentTimeMillis() - pauseStartTime;
            // Adjust time remaining based on pause duration
            timeRemaining = Math.max(0, timeRemaining - (int)(pauseDuration / 1000));
            startTimer();
        }
    }
    
    public void shutdown() {
        if (timerTask != null && !timerTask.isDone()) {
            timerTask.cancel(true);
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
