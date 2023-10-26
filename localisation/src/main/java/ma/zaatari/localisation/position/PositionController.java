package ma.zaatari.localisation.position;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/positions")
public class PositionController {

    @Autowired
    private PositionService positionService;

    // GET methods

    @GetMapping("/all")
    public List<Position> findAll() {
        return positionService.findAll();
    }

    @GetMapping("/findById")
    public Position findById(@RequestParam Integer id) {
        return positionService.findById(id);
    }

    // POST methods

    @PostMapping("/create")
    public void createPosition(@RequestBody Position pos) {
        positionService.createPosition(pos);
    }

    // PUT methods
    @PutMapping("/update")
    public void updatePosition(@RequestParam Integer id,
            @RequestBody Position pos) {
        positionService.update(id, pos);
    }

    // DELETE methods
    @DeleteMapping("/delete")
    public void deletePosition(@RequestParam Integer id) {
        positionService.delete(id);
    }

}
