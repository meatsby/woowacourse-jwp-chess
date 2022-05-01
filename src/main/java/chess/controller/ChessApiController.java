package chess.controller;

import chess.domain.position.Position;
import chess.dto.request.MoveDto;
import chess.dto.response.GameStatusDto;
import chess.dto.response.StatusDto;
import chess.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
@RequestMapping("/room/{roomId}")
public class ChessApiController {

    private final GameService gameService;

    public ChessApiController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/move")
    public ResponseEntity<GameStatusDto> movePiece(@PathVariable("roomId") int roomId, @RequestBody MoveDto moveDto) {
        if (gameService.isEnd(roomId)) {
            throw new IllegalArgumentException("게임이 이미 끝났다.");
        }
        gameService.move(roomId, Position.of(moveDto.getStart()), Position.of(moveDto.getTarget()));
        return ResponseEntity.ok(new GameStatusDto(gameService.isEnd(roomId)));
    }

    @PostMapping("/end")
    public ResponseEntity<Void> endGame(@PathVariable("roomId") int id, @RequestParam("password") String password) {
        if (!gameService.isEnd(id)) {
            throw new IllegalArgumentException("진행중인 게임은 삭제할 수 없어~");
        }
        if (!gameService.delete(id, password)) {
            throw new IllegalArgumentException("게임 삭제에 실패하였습니다.");
        }
        return ResponseEntity.ok(null);
    }

    @GetMapping("/status")
    public StatusDto showStatus(@PathVariable("roomId") int id) {
        return gameService.status(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}