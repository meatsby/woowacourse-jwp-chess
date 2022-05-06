package chess;

import chess.dao.ChessBoardDao;
import chess.dao.ChessPieceDao;
import chess.dao.ChessPositionDao;
import chess.domain.Game;
import chess.domain.game.BoardInitializer;
import chess.domain.game.ChessBoard;
import chess.domain.game.ChessBoardInitializer;
import chess.domain.pieces.Color;
import chess.domain.pieces.Piece;
import chess.domain.position.Position;
import chess.dto.request.MoveDto;
import chess.entities.GameEntity;
import chess.entities.MemberEntity;
import chess.entities.PieceEntity;
import io.restassured.RestAssured;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringChessApplicationTests {

    @Autowired
    ChessBoardDao boardDao;

    @Autowired
    ChessPositionDao positionDao;

    @Autowired
    ChessPieceDao pieceDao;

    @LocalServerPort
    int port;

    int boardId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        GameEntity board = boardDao.save(
                new GameEntity("방1", List.of(new MemberEntity("쿼리치"), new MemberEntity("코린")), "1111",
                        new Game(new ChessBoard(new ChessBoardInitializer()), Color.WHITE)));
        this.boardId = board.getId();
        BoardInitializer boardInitializer = new ChessBoardInitializer();
        final Map<Position, Piece> initialize = boardInitializer.initialize();
        positionDao.saveAll(board.getId());
        for (Position position : initialize.keySet()) {
            int lastPositionId = positionDao.getIdByColumnAndRowAndBoardId(position.getColumn(), position.getRow(),
                    board.getId());
            final Piece piece = initialize.get(position);
            pieceDao.save(new PieceEntity(piece.getColor(), piece.getType(), lastPositionId));
        }
    }

    @DisplayName("이동 명령어로 말을 움직인다.")
    @Test
    void movePiece() {
        MoveDto moveDto = new MoveDto("a2", "a4");

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(moveDto)
                .when().post("/room/" + boardId + "/move")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("현재 점수를 보여준다.")
    @Test
    void showStatus() {
        RestAssured.given().log().all()
                .when().get("/room/" + boardId + "/status")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("비밀번호가 비어있는 경우 예외가 발생한다.")
    @Test
    void passwordEmpty() {
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body("title=쿼리치&firstMemberName=쿼리치&secondMemberName=코린&password=")
                .when().post("/room")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @AfterEach
    void setDown() {
        boardDao.deleteAll();
        positionDao.deleteAll();
    }
}
