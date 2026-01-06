import React from 'react';
import { Container, Typography, Box, Paper } from '@mui/material';

const TermsOfService: React.FC = () => {
  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Terms of Service
        </Typography>
        <Typography variant="body1" sx={{ mb: 2 }}>
          Last updated: {new Date().toLocaleDateString()}
        </Typography>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            1. Acceptance of Terms
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            By accessing and using Course Genius, you agree to be bound by these Terms of Service and all applicable laws and regulations.
          </Typography>
        </Box>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            2. Description of Service
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            Course Genius is an academic planning tool that helps students manage their courses and calculate GPAs. The service is provided "as is" and may be modified or discontinued at any time.
          </Typography>
        </Box>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            3. User Responsibilities
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            You agree to:
          </Typography>
          <ul>
            <li>Provide accurate information</li>
            <li>Maintain the security of your account</li>
            <li>Use the service in compliance with all laws</li>
            <li>Not misuse or abuse the service</li>
          </ul>
        </Box>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            4. Disclaimer of Warranties
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            The service is provided without warranties of any kind, either express or implied. We do not guarantee the accuracy of GPA calculations or course recommendations.
          </Typography>
        </Box>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            5. Limitation of Liability
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            We shall not be liable for any indirect, incidental, special, consequential, or punitive damages resulting from your use of the service.
          </Typography>
        </Box>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            6. Intellectual Property
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            All content and materials available through the service are protected by intellectual property rights. You may not use, reproduce, or distribute any content without permission.
          </Typography>
        </Box>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            7. Termination
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            We reserve the right to terminate or suspend your access to the service at any time, without notice, for any reason.
          </Typography>
        </Box>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            8. Changes to Terms
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            We reserve the right to modify these terms at any time. We will notify users of any material changes.
          </Typography>
        </Box>

        <Box sx={{ my: 3 }}>
          <Typography variant="h6" gutterBottom>
            9. Contact Information
          </Typography>
          <Typography variant="body1" sx={{ mb: 2 }}>
            For questions about these Terms, please contact us at:
            <br />
            Email: jrlockhart04@gmail.com
          </Typography>
        </Box>
      </Paper>
    </Container>
  );
};

export default TermsOfService; 