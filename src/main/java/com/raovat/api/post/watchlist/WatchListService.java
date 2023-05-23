package com.raovat.api.post.watchlist;

import com.raovat.api.appuser.AppUser;
import com.raovat.api.appuser.AppUserService;
import com.raovat.api.config.JwtService;
import com.raovat.api.post.Post;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchListService {

    private final WatchListRepository watchListRepository;
    private final AppUserService appUserService;
    private final JwtService jwtService;

    public WatchListDTO switchPostFollowStatus(HttpServletRequest request, Long id) {
        AppUser appUser = appUserService.getCurrentUser(request);

        WatchList watchList = watchListRepository.findByPostIdAndAppUserEmail(id, appUser.getEmail());
        boolean followed;

        if (watchList == null) {
            watchListRepository.save(
                    WatchList.builder()
                            .appUser(appUser)
                            .post(
                                    Post.builder()
                                            .id(id)
                                            .build())
                            .build());
            followed = true;
        }
        else {
            watchListRepository.delete(watchList);
            followed = false;
        }
        return new WatchListDTO(followed);
    }

    public List<WatchList> findByAppUserEmail(String email) {
        return watchListRepository.findByAppUserEmail(email);
    }
}
