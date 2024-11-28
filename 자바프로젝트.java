package JavaGame;

import javax.swing.*; // 화면을 만드는 데 필요한 클래스
import java.awt.*; // 그래픽 관련 클래스
import java.awt.event.*; // 버튼 클릭이나 키보드 입력 처리를 위한 클래스
import java.io.*; // 파일 읽기와 쓰기를 위한 클래스
import java.util.*; // 리스트, 맵 등 자료구조를 위한 클래스
import java.util.Timer; // 타이머 기능을 사용하기 위한 클래스
import java.util.List; // 리스트 자료구조를 위한 클래스

// 게임 전체를 관리하는 클래스
public class 자바프로젝트 {

    // 주요 게임 화면과 변수들
    private JFrame frame; // 게임의 기본 창
    private CardLayout cardLayout; // 여러 화면을 쉽게 전환하기 위한 레이아웃
    private JPanel mainPanel; // 모든 화면(패널)을 포함하는 패널
    private GamePanel gamePanel; // 실제 게임을 그리는 화면
    private int playerX, playerY; // 플레이어의 현재 위치 (행과 열)
    private int stage = 1; // 현재 스테이지 번호
    private int playerHealth = 3; // 플레이어의 체력 (처음에 3)
    private int score = 0; // 플레이어의 점수
    private int timeLeft; // 남은 제한 시간
    private char[][] map; // 게임 맵 데이터
    private Timer timer; // 제한 시간을 관리하는 타이머
    private static final int TILE_SIZE = 40; // 화면에 그려지는 각 타일의 크기
    private static final String SCORE_FILE = "scores.txt"; // 점수 저장 파일 이름

    // 게임을 시작할 때 실행되는 코드
    public 자바프로젝트() {
        // 창을 설정
        frame = new JFrame("Tomb of the Mask Styled Game"); // 창 제목
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창을 닫으면 프로그램 종료
        frame.setSize(600, 600); // 창 크기 설정
        frame.setResizable(false); // 창 크기를 조정할 수 없게 설정

        // 화면 전환을 관리하는 레이아웃을 만들기
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 여러 화면 추가	
        mainPanel.add(createStartScreen(), "StartScreen"); // 시작 화면
        mainPanel.add(createDescriptionScreen(), "DescriptionScreen"); // 설명 화면
        mainPanel.add(createGameScreen(), "GameScreen"); // 실제 게임 화면
        mainPanel.add(createGameOverScreen(), "GameOverScreen"); // 게임 오버 화면
        mainPanel.add(createWinScreen(), "WinScreen"); // 게임 승리 화면

        frame.add(mainPanel); // 창에 메인 패널을 추가
        frame.setVisible(true); // 창을 화면에 보이게 설정
    }

    // 시작 화면 만드는 함수
    private JPanel createStartScreen() {
        JPanel panel = new JPanel(new BorderLayout()); // 레이아웃을 사용해 화면 구성
        JLabel title = new JLabel("Slide to Escape!", SwingConstants.CENTER); // 화면 제목
        title.setFont(new Font("Arial", Font.BOLD, 24)); // 제목 글꼴 설정
        panel.add(title, BorderLayout.CENTER); // 제목을 화면 중앙에 배치

        JPanel buttonPanel = new JPanel(); // 버튼들을 배치할 패널 생성
        JButton startButton = new JButton("게임 시작"); // "게임 시작" 버튼
        startButton.addActionListener(e -> {
            // "게임 시작" 버튼을 누르면 초기화 후 게임 화면으로 이동
            stage = 1; // 스테이지를 1로 초기화
            playerHealth = 3; // 체력을 3으로 초기화
            score = 0; // 점수를 0으로 초기화
            loadMap(stage); // 첫 번째 스테이지의 맵을 불러오기
            startTimer(60); // 제한 시간 60초 설정
            playerX = 1; // 플레이어의 시작 위치 설정
            playerY = 1;
            cardLayout.show(mainPanel, "GameScreen"); // 게임 화면으로 전환
            gamePanel.requestFocusInWindow(); // 키보드 입력 받을 준비
        });

        JButton descriptionButton = new JButton("게임 설명"); // "게임 설명" 버튼
        descriptionButton.addActionListener(e -> cardLayout.show(mainPanel, "DescriptionScreen")); // 설명 화면으로 이동

        JButton rankButton = new JButton("게임 순위"); // "게임 순위" 버튼
        rankButton.addActionListener(e -> displayRankings()); // 순위 표시 함수 실행

        JButton exitButton = new JButton("게임 종료"); // "게임 종료" 버튼
        exitButton.addActionListener(e -> System.exit(0)); // 프로그램 종료

        // 버튼들 패널에 추가
        buttonPanel.add(startButton);
        buttonPanel.add(descriptionButton);
        buttonPanel.add(rankButton);
        buttonPanel.add(exitButton);
        panel.add(buttonPanel, BorderLayout.SOUTH); // 버튼 패널 아래쪽에 배치

        return panel; // 생성한 시작 화면 패널을 반환
    }

    // 게임 설명 화면 만드는 함수
    private JPanel createDescriptionScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Game Description", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        // 게임 설명을 텍스트로 작성
        JTextArea description = new JTextArea();
        description.setText(
                "                                                                             ★게임 설명★\n"
                + "                                     1. WASD 키로 캐릭터(파란색 공)를 이동합니다.\n"
                + "                                     2. 장애물(검은색 블록)에 부딪히면 체력이 감소합니다.\n"
                + "                                     3. 체력이 0이 되면 게임이 종료됩니다.\n"
                + "                                     4. 목표 지점(초록색 블록)에 도달하면 다음 스테이지로 이동합니다.\n"
                + "                                     5. 총 3개의 스테이지로 구성되어 있습니다.\n"
                + "                                     6. 각 스테이지는 제한 시간이 있습니다.\n"
                + "                                     7. 제한시간안에 스테이지를 통과하지 못하면 게임이 종료됩니다.\n"
                + "                                     8. 점수는 체력과 남은 시간에 따라 계산됩니다."
        );
        description.setEditable(false); // 텍스트를 수정할 수 없게 설정
        description.setLineWrap(true); // 텍스트 줄바꿈 허용
        description.setWrapStyleWord(true); // 단어 단위로 줄바꿈
        panel.add(new JScrollPane(description), BorderLayout.CENTER); // 설명 텍스트를 중앙에 추가

        JButton backButton = new JButton("뒤로가기"); // "뒤로가기" 버튼
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "StartScreen")); // 시작 화면으로 이동
        panel.add(backButton, BorderLayout.SOUTH); // 버튼 아래쪽에 배치

        return panel; // 생성한 설명 화면 패널 반환
    }

    // 게임 실행 화면 생성하는 함수
    private JPanel createGameScreen() {
        gamePanel = new GamePanel(); // 게임을 그릴 패널 생성
        gamePanel.setFocusable(true); // 키보드 입력 가능 설정
        gamePanel.requestFocusInWindow(); // 게임 화면을 키보드 입력 대상으로 설정

        // 키보드 입력 이벤트 처리
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                movePlayer(e.getKeyCode()); // 플레이어 이동 함수 호출
                gamePanel.repaint(); // 화면을 새로 그리기
            }
        });

        return gamePanel; // 게임 화면 패널 반환
    }

    // 게임 오버 화면 만드는 함수
    private JPanel createGameOverScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel gameOverLabel = new JLabel("Game Over :(", SwingConstants.CENTER); // 게임 오버 메시지
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 24)); // 메시지 폰트 설정
        panel.add(gameOverLabel, BorderLayout.CENTER); // 메시지를 중앙에 배치

        JPanel buttonPanel = new JPanel(); // 버튼을 넣을 패널 생성
        JButton mainMenuButton = new JButton("메인 화면으로"); // "메인 화면으로" 버튼
        mainMenuButton.addActionListener(e -> cardLayout.show(mainPanel, "StartScreen")); // 메인 화면으로 이동

        JButton restartButton = new JButton("다시 시작"); // "다시 시작" 버튼
        restartButton.addActionListener(e -> {
            stage = 1; // 스테이지를 초기화
            playerHealth = 3; // 체력을 초기화
            score = 0; // 점수를 초기화
            loadMap(stage); // 첫 번째 스테이지의 맵을 로드
            startTimer(60); // 제한 시간 60초로 설정
            playerX = 1; // 플레이어 시작 위치 초기화
            playerY = 1;
            cardLayout.show(mainPanel, "GameScreen"); // 게임 화면으로 이동
            gamePanel.requestFocusInWindow(); // 키보드 입력 준비
        });

        buttonPanel.add(mainMenuButton); // 버튼 패널에 "메인 화면으로" 버튼 추가
        buttonPanel.add(restartButton); // 버튼 패널에 "다시 시작" 버튼 추가
        panel.add(buttonPanel, BorderLayout.SOUTH); // 버튼 패널을 아래쪽에 배치

        return panel; // 생성된 게임 오버 화면 반환
    }

    // 게임에서 승리했을 때 화면을 만드는 함수
    private JPanel createWinScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel winLabel = new JLabel("Game Clear :)", SwingConstants.CENTER); // 승리 메시지
        winLabel.setFont(new Font("Arial", Font.BOLD, 24)); // 메시지 폰트 설정
        panel.add(winLabel, BorderLayout.CENTER); // 메시지를 중앙에 배치

        // 이름 입력 필드와 점수 저장 버튼 추가
        JTextField nameField = new JTextField("이름을 입력하세요"); // 이름 입력 필드
        JButton saveScoreButton = new JButton("점수 저장"); // "점수 저장" 버튼
        saveScoreButton.addActionListener(e -> {
            String name = nameField.getText().trim(); // 입력된 이름 가져오기
            if (!name.isEmpty()) { // 이름이 비어있지 않으면 점수 저장
                saveScore(name, score); // 이름과 점수 저장
                JOptionPane.showMessageDialog(frame, "점수가 저장되었습니다!"); // 저장 성공 메시지
                cardLayout.show(mainPanel, "StartScreen"); // 메인 화면으로 이동
            } else {
                JOptionPane.showMessageDialog(frame, "이름을 입력해주세요."); // 이름 입력 요청 메시지
            }
        });

        // 입력 필드와 버튼 패널에 추가
        JPanel inputPanel = new JPanel();
        inputPanel.add(nameField); // 이름 입력 필드 추가
        inputPanel.add(saveScoreButton); // "점수 저장" 버튼 추가
        panel.add(inputPanel, BorderLayout.SOUTH); // 입력 패널 아래쪽에 배치

        return panel; // 생성된 승리 화면 반환
    }

    // 스테이지에 맞는 맵을 불러오는 함수
    private void loadMap(int stage) {
        if (stage == 1) { // 스테이지 1
            map = new char[][]{
                {'1', '1', '1', '1', '1', '1', '#', '1'},
                {'1', '.', '.', '.', '.', '.', '.', '1'},
                {'1', '1', '#', '#', '#', '1', '.', '1'},
                {'1', '1', '.', '.', '.', '.', '.', '1'},
                {'1', '.', '.', '.', '1', '.', '.', '1'},
                {'#', '.', '.', 'G', '1', '.', '1', '1'},
                {'1', '.', '.', '.', '.', '.', '1', '1'},
                {'1', '1', '1', '#', '#', '#', '#', '1'},
            };
        } else if (stage == 2) { // 스테이지 2
            map = new char[][]{
                {'1', '#', '1', '1', '1', '1', '1', '1', '1', '1'},
                {'#', '.', '#', '.', '.', '.', '.', '.', '.', '1'},
                {'1', '.', '1', '#', '#', '.', '1', '1', 'G', '1'},
                {'1', '.', '.', '.', '.', '.', '.', '.', '1', '1'},
                {'1', '1', '.', '.', '.', '.', '.', '.', '.', '1'},
                {'1', '1', '.', '#', '#', '.', '.', '.', '.', '1'},
                {'1', '1', '.', '#', '#', '.', '1', '.', '.', '1'},
                {'1', '.', '.', '.', '.', '.', '1', '.', '.', '1'},
                {'1', '.', '.', '.', '.', '1', '#', '.', '.', '1'},
                {'1', '1', '1', '1', '1', '1', '1', '1', '1', '1'},
            };
        } else if (stage == 3) { // 스테이지 3
            map = new char[][]{
                {'#', '#', '#', '.', '#', '.', '1', '#', '#', '#', '#', '#'},
                {'#', '.', '#', '.', 'G', '.', '.', '1', '.', '.', '.', '#'},
                {'#', '.', '#', '.', '.', '.', '.', '.', '.', '.', '1', '#'},
                {'#', '.', '.', '.', '1', '.', '.', '.', '.', '.', '.', '#'},
                {'#', '1', '#', '.', '.', '.', '.', '.', '.', '.', '.', '1'},
                {'#', '.', '.', '.', '.', '1', '.', '.', '.', '.', '.', '#'},
                {'#', '.', '.', '.', '.', '#', '.', '.', '.', '1', '.', '#'},
                {'#', '.', '.', '.', '.', '#', '.', '.', '.', '.', '.', '#'},
                {'1', '.', '.', '.', '.', '.', '1', '.', '.', '.', '.', '#'},
                {'#', '#', '#', '1', '#', '#', '1', '1', '#', '1', '1', '#'},
            };
        }
    }

    // 제한 시간 관리하는 함수
    private void startTimer(int seconds) {
        timeLeft = seconds; // 제한 시간 설정
        timer = new Timer(); // 새로운 타이머 생성
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeLeft--; // 1초 감소
                gamePanel.repaint(); // 화면 새로 그리기
                if (timeLeft <= 0) { // 시간이 다 되면
                    timer.cancel(); // 타이머 중지
                    cardLayout.show(mainPanel, "GameOverScreen"); // 게임 오버 화면으로 이동
                }
            }
        }, 1000, 1000); // 1초마다 실행
    }

    // 플레이어 이동시키는 함수
    private void movePlayer(int keyCode) {
        int dx = 0, dy = 0; // 이동 방향 설정

        // 키보드 입력에 따라 이동 방향 설정
        switch (keyCode) {
            case KeyEvent.VK_W: dx = -1; break; // 위쪽
            case KeyEvent.VK_S: dx = 1; break; // 아래쪽
            case KeyEvent.VK_A: dy = -1; break; // 왼쪽
            case KeyEvent.VK_D: dy = 1; break; // 오른쪽
            default: return; // 다른 키는 무시
        }

        while (true) {
            int newX = playerX + dx; // 새로운 X 위치
            int newY = playerY + dy; // 새로운 Y 위치

            // 맵의 경계를 벗어나거나 벽에 닿으면 멈춤
            if (newX < 0 || newY < 0 || newX >= map.length || newY >= map[0].length || map[newX][newY] == '1') {
                break;
            }

            // 장애물에 닿으면 체력 감소
            if (map[newX][newY] == '#') {
                playerHealth--;
                if (playerHealth <= 0) { // 체력이 0이 되면 게임 오버
                    timer.cancel();
                    cardLayout.show(mainPanel, "GameOverScreen");
                }
                return;
            }

            // 목표 지점에 도달하면 다음 스테이지로 이동
            if (map[newX][newY] == 'G') {
                timer.cancel(); // 타이머 중지
                score += playerHealth * 100 + timeLeft; // 점수 계산
                stage++; // 다음 스테이지로 이동
                if (stage > 3) {
                    cardLayout.show(mainPanel, "WinScreen"); // 마지막 스테이지 클리어 시 승리 화면
                } else {
                    loadMap(stage); // 다음 스테이지 맵 로드
                    startTimer(stage * 60); // 다음 스테이지 제한 시간 설정
                    playerX = 1; // 플레이어 위치 초기화
                    playerY = 1;
                }
                return;
            }

            // 빈 공간으로 이동
            map[playerX][playerY] = '.'; // 현재 위치 빈 공간으로 변경
            playerX = newX; // 새로운 위치로 업데이트
            playerY = newY;
        }
    }

    // 점수 보여주는 함수
    private void displayRankings() {
        File scoreFile = new File(SCORE_FILE);

        if (!scoreFile.exists()) { // 파일이 없으면 생성
            try {
                scoreFile.createNewFile();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "순위 파일을 생성할 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(scoreFile))) {
            List<String> scores = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) { // 데이터가 "이름 점수" 형식인지 확인
                    try {
                        Integer.parseInt(parts[1]); // 점수가 숫자인지 확인
                        scores.add(line); // 유효한 데이터만 추가
                    } catch (NumberFormatException ignored) {
                        // 점수가 숫자가 아닌 경우 무시
                    }
                }
            }

            // 점수를 내림차순으로 정렬
            scores.sort((a, b) -> Integer.compare(Integer.parseInt(b.split(" ")[1]), Integer.parseInt(a.split(" ")[1])));

            // 순위를 출력
            if (scores.isEmpty()) { // 점수가 없으면 알림
                JOptionPane.showMessageDialog(frame, "순위 데이터가 없습니다.", "게임 순위", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder rankingText = new StringBuilder("순위:\n");
                for (int i = 0; i < Math.min(10, scores.size()); i++) {
                    rankingText.append((i + 1)).append(". ").append(scores.get(i)).append("\n");
                }
                JOptionPane.showMessageDialog(frame, rankingText.toString(), "게임 순위", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "순위 데이터를 불러올 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 점수 저장하는 함수
    private void saveScore(String name, int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE, true))) {
            writer.write(name + " " + score + "\n"); // 이름과 점수를 파일에 저장
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "점수를 저장할 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 게임 화면 그리는 패널 클래스
    class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 맵 그리기
            for (int row = 0; row < map.length; row++) {
                for (int col = 0; col < map[row].length; col++) {
                    int x = col * TILE_SIZE;
                    int y = row * TILE_SIZE;

                    if (map[row][col] == '1') g.setColor(Color.GRAY); // 벽
                    else if (map[row][col] == '#') g.setColor(Color.BLACK); // 장애물
                    else if (map[row][col] == '.') g.setColor(Color.WHITE); // 빈 공간
                    else if (map[row][col] == 'G') g.setColor(Color.GREEN); // 목표 지점
                    g.fillRect(x, y, TILE_SIZE, TILE_SIZE); // 타일 채우기
                }
            }

            // 플레이어 파란색 원으로 그리기
            g.setColor(Color.BLUE);
            g.fillOval(playerY * TILE_SIZE + 5, playerX * TILE_SIZE + 5, TILE_SIZE - 10, TILE_SIZE - 10);

            // 체력과 남은 시간 표시
            g.setColor(Color.RED);
            g.drawString("체력: " + playerHealth, 10, 20);
            g.drawString("남은 시간: " + timeLeft + "초", 10, 40);
        }
    }

    // 메인 함수
    public static void main(String[] args) {
        SwingUtilities.invokeLater(자바프로젝트::new); // 프로그램 실행
    }
}