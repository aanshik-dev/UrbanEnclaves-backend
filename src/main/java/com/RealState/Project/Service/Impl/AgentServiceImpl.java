package com.RealState.Project.Service.Impl;

import com.RealState.Project.DTO.AgentDashboardDTO;
import com.RealState.Project.DTO.AgentPerformanceDTO;
import com.RealState.Project.DTO.PropertyForOtherTableResponseDTO;
import com.RealState.Project.Entity.*;
import com.RealState.Project.Entity.Type.Status;
import com.RealState.Project.Entity.Type.UserType;
import com.RealState.Project.Exception.AgentNotFoundException;
import com.RealState.Project.Exception.UserNotFoundException;
import com.RealState.Project.Repository.*;
import com.RealState.Project.Service.AgentService;
import com.RealState.Project.Utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {
    private final AgentRepository agentRepository;
    private final TransactionRepository transactionRepository;
    private final ListingTokenRepository listingTokenRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final PerformanceRepository performanceRepository;

    private PropertyForOtherTableResponseDTO getBestPropertySold(Long agentId){

        List<Transaction> transactions =
                transactionRepository.findTopTransactionByAgent(agentId);

        if(transactions.isEmpty()){
            return null;
        }

        Transaction top = transactions.get(0);

        ListingToken listing = top.getToken();
        Property property = listing.getPid();

        return new PropertyForOtherTableResponseDTO(
                property.getId(),
                property.getHouseNo(),
                property.getDescription(),
                property.getLocality(),
                property.getArea(),
                property.getCity(),
                property.getPin(),
                property.getSize(),
                property.getType(),
                property.getBHK()
        );
    }

    private Agent getCurrentAgent(){

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return agentRepository.findByUser(user)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found"));
    }


    @Override
    public AgentDashboardDTO getMyDashboard() {

        Agent agent = getCurrentAgent();

        System.out.println(agent.getId());

        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);

        Long totalListings =
                listingTokenRepository.countByAgent(agent);

        Long activeListings =
                listingTokenRepository.countByAgentAndStatus(agent, Status.ACTIVE);

        Long soldListings =
                listingTokenRepository.countByAgentAndStatus(agent,Status.INACTIVE);


        Long totalDeals =
                transactionRepository.countByAgent(agent);

        Long monthlyDeals =
                transactionRepository.dealsAfter(agent,monthStart);


        Double totalRevenue =
                transactionRepository.getTotalRevenue(agent);

        Double monthlyRevenue =
                transactionRepository.revenueAfter(agent,monthStart);


        Double totalCommission =
                totalRevenue * agent.getCommissionRate() / 100;

        Double monthlyCommission =
                monthlyRevenue * agent.getCommissionRate() / 100;


        Double avgDeal =
                totalDeals == 0 ? 0 : totalRevenue / totalDeals;


        Double conversion =
                totalListings == 0 ? 0 :
                        (double) totalDeals / totalListings * 100;


        Performance performance =
                performanceRepository.findByAgent(agent)
                        .orElse(null);

        Float rating = 0f;
        Float performanceScore = 0f;

        if(performance != null){
            System.out.println(performance.getAgent().getId());

            rating = performance.getUser_rating();
            System.out.println(rating);

            performanceScore = performance.getScore();
        }


        return AgentDashboardDTO.builder()
                .totalListings(totalListings)
                .activeListings(activeListings)
                .soldListings(soldListings)
                .rating(rating)
                .performanceScore(performanceScore)
                .totalDeals(totalDeals)
                .dealsThisMonth(monthlyDeals)

                .totalRevenue(totalRevenue)
                .monthlyRevenue(monthlyRevenue)

                .totalCommission(totalCommission)
                .monthlyCommission(monthlyCommission)

                .avgDealValue(avgDeal)
                .conversionRate(conversion)
                .bestPropertySold(getBestPropertySold(agent.getId()))

                .build();
    }

    @Override
    public AgentPerformanceDTO getMyPerformance() {

        Agent agent = getCurrentAgent();
        return convertToDTO(agent);
    }

    private void validateAgentAccess(Agent agent){

        User user = securityUtil.getCurrentUser();

        // Admin can access all
        if(user.getUserProfile().getUserType() == UserType.ADMIN){
            return;
        }

        // Office check
        if(user.getUserProfile().getUserType() == UserType.OFFICE){

            Office office = securityUtil.getCurrentOffice();

            if(!agent.getOffice().getId().equals(office.getId())){
                throw new RuntimeException("Access denied");
            }
        }
    }


    @Override
    public AgentPerformanceDTO getAgentPerformance(Long agentId) {

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found"));

        validateAgentAccess(agent);

        return convertToDTO(agent);
    }


    @Override
    public List<AgentPerformanceDTO> getAllPerformance() {

        User user = securityUtil.getCurrentUser();

        List<Agent> agents;

        if(user.getUserProfile().getUserType() == UserType.ADMIN){

            agents = agentRepository.findAll();

        }else{

            Office office = securityUtil.getCurrentOffice();

            agents = agentRepository.findByOffice(office);
        }

        return agents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }




    public AgentPerformanceDTO convertToDTO(
            Agent agent
    ){
        int activeDeals = listingTokenRepository.countActiveDeals(agent);
        Performance performance =
                performanceRepository
                        .findByAgent(agent).orElseThrow(
                                () -> new RuntimeException("Performance not found")
                        );

        return AgentPerformanceDTO.builder()

                // Agent Info
                .agentId(agent.getId())
                .agentName(
                        agent.getUser()
                                .getUserProfile()
                                .getName()
                )
                .agentStatus(agent.getStatus().name())
                .profileUrl(
                        agent.getUser()
                                .getUserProfile()
                                .getProfileURL()
                )
                .phone(
                        String.valueOf(
                                agent.getUser()
                                        .getUserProfile()
                                        .getPhone()
                        )
                )
                .email(agent.getUser().getEmail())
                .experience(String.valueOf(agent.getExperience()))
                .commissionRate(String.valueOf(agent.getCommissionRate()))

                // Performance
                .total_deals(performance.getTotal_deals())
                .totalSales(performance.getTotalSales())
                .score(performance.getScore())
                .user_rating(performance.getUser_rating())
                .deals_left(performance.getDeals_left())

                // Active Deals
                .activeDeals(activeDeals)

                .build();
    }
}
