package bank.rest.app.bankrestapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/admin")
class AdminController {

    @GetMapping("/dashboard")
    public ResponseEntity<?> getAdminDashboard() {

        return ok().build() ;
    }
}
